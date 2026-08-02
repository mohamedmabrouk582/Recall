package com.mabrouk.recall.data.ai

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.mabrouk.recall.data.ai.download.ModelDownloadWorker
import com.mabrouk.recall.data.ai.model.ModelAccelerator
import com.mabrouk.recall.data.ai.model.ModelIds
import com.mabrouk.recall.data.ai.model.ModelRegistry
import com.mabrouk.recall.data.ai.model.ModelSpec
import com.mabrouk.recall.data.ai.model.ModelStatus
import com.mabrouk.recall.data.ai.model.SmokeInferenceResult
import com.mabrouk.recall.data.ai.runtime.LiteRtModelLoader
import com.mabrouk.recall.data.ai.store.ModelStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

interface ModelManager {
    fun observe(modelId: String): Flow<ModelStatus>
    fun observeAll(): Flow<Map<String, ModelStatus>>
    suspend fun ensureModel(
        modelId: String,
        preferredAccelerator: ModelAccelerator = ModelAccelerator.CPU,
    ): ModelStatus

    suspend fun runSmokeInference(modelId: String): SmokeInferenceResult
}

@Singleton
class DefaultModelManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val registry: ModelRegistry,
    private val modelStore: ModelStore,
    private val loader: LiteRtModelLoader,
) : ModelManager {

    private val statuses = MutableStateFlow<Map<String, ModelStatus>>(
        registry.all().associate { it.id to ModelStatus.NotDownloaded as ModelStatus },
    )

    override fun observe(modelId: String): Flow<ModelStatus> =
        statuses.map { it[modelId] ?: ModelStatus.NotDownloaded }

    override fun observeAll(): Flow<Map<String, ModelStatus>> = statuses.asStateFlow()

    override suspend fun ensureModel(
        modelId: String,
        preferredAccelerator: ModelAccelerator,
    ): ModelStatus {
        val spec = registry.get(modelId)
            ?: return emitError(modelId, "Unknown model id: $modelId")

        return try {
            if (!modelStore.existsAndValid(spec)) {
                downloadAndVerify(spec)
            }
            if (!modelStore.existsAndValid(spec)) {
                return emitError(modelId, "Model file missing after download")
            }
            setStatus(modelId, ModelStatus.Verifying)
            val path = modelStore.modelFile(spec).absolutePath
            val loaded = loader.load(modelId, path, preferredAccelerator)
            val ready = ModelStatus.Ready(
                path = loaded.path,
                accelerator = loaded.accelerator,
                coldLoadMs = loaded.coldLoadMs,
            )
            setStatus(modelId, ready)
            ready
        } catch (t: Throwable) {
            emitError(modelId, t.message ?: "ensureModel failed", t)
        }
    }

    override suspend fun runSmokeInference(modelId: String): SmokeInferenceResult {
        if (statuses.value[modelId] !is ModelStatus.Ready) {
            ensureModel(modelId)
        }
        return loader.runSmokeInference(modelId)
    }

    private suspend fun downloadAndVerify(spec: ModelSpec) {
        setStatus(spec.id, ModelStatus.Downloading(0f))
        val constraints = if (spec.uri.startsWith("https://") || spec.uri.startsWith("http://")) {
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        } else {
            Constraints.NONE
        }

        val request = OneTimeWorkRequestBuilder<ModelDownloadWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .setInputData(
                workDataOf(
                    ModelDownloadWorker.KEY_MODEL_ID to spec.id,
                    ModelDownloadWorker.KEY_VERSION to spec.version,
                ),
            )
            .build()

        val workName = ModelDownloadWorker.uniqueWorkName(spec.id, spec.version)
        val workManager = WorkManager.getInstance(context)
        // REPLACE: ensureModel only enqueues when the local file is missing/corrupt.
        workManager.enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            request,
        )

        val terminal = workManager.getWorkInfosForUniqueWorkFlow(workName)
            .onEach { infos ->
                val info = infos.firstOrNull() ?: return@onEach
                when (info.state) {
                    WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> {
                        val progress = info.progress
                            .getFloat(ModelDownloadWorker.KEY_PROGRESS, 0f)
                            .coerceIn(0f, 1f)
                        setStatus(spec.id, ModelStatus.Downloading(progress))
                    }
                    else -> Unit
                }
            }
            .first { infos -> infos.any { it.state.isFinished } }
            .first { it.state.isFinished }

        when (terminal.state) {
            WorkInfo.State.SUCCEEDED -> setStatus(spec.id, ModelStatus.Verifying)
            WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                val message = terminal.outputData
                    .getString(ModelDownloadWorker.KEY_ERROR)
                    ?: "Download ${terminal.state.name.lowercase()}"
                throw IllegalStateException(message)
            }
            else -> error("Unexpected terminal work state: ${terminal.state}")
        }
    }

    private fun setStatus(modelId: String, status: ModelStatus) {
        statuses.update { it + (modelId to status) }
    }

    private fun emitError(
        modelId: String,
        message: String,
        cause: Throwable? = null,
    ): ModelStatus {
        val error = ModelStatus.Error(message, cause)
        setStatus(modelId, error)
        return error
    }

    companion object {
        val DefaultModelId: String = ModelIds.PLACEHOLDER_SINE
    }
}
