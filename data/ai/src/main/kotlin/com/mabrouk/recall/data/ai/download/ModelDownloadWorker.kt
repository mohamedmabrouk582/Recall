package com.mabrouk.recall.data.ai.download

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mabrouk.recall.data.ai.model.ModelRegistry
import com.mabrouk.recall.data.ai.store.ModelStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

@HiltWorker
class ModelDownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val modelRegistry: ModelRegistry,
    private val modelStore: ModelStore,
    private val okHttpClient: OkHttpClient,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val modelId = inputData.getString(KEY_MODEL_ID)
            ?: return@withContext Result.failure(workDataOf(KEY_ERROR to "Missing model id"))
        val version = inputData.getString(KEY_VERSION)
            ?: return@withContext Result.failure(workDataOf(KEY_ERROR to "Missing version"))

        val spec = modelRegistry.get(modelId)
            ?: return@withContext Result.failure(workDataOf(KEY_ERROR to "Unknown model $modelId"))
        if (spec.version != version) {
            return@withContext Result.failure(
                workDataOf(KEY_ERROR to "Version mismatch for $modelId"),
            )
        }

        if (modelStore.existsAndValid(spec)) {
            setProgress(workDataOf(KEY_PROGRESS to 1f))
            return@withContext Result.success(
                workDataOf(KEY_PATH to modelStore.modelFile(spec).absolutePath),
            )
        }

        val temp = modelStore.tempFile(spec)
        temp.parentFile?.mkdirs()
        if (temp.exists()) temp.delete()

        try {
            when {
                spec.uri.startsWith(ASSET_SCHEME) -> copyFromAssets(spec.uri.removePrefix(ASSET_SCHEME), temp)
                spec.uri.startsWith("https://") || spec.uri.startsWith("http://") ->
                    downloadHttps(spec.uri, temp, spec.sizeBytes)
                else -> error("Unsupported model URI scheme: ${spec.uri}")
            }
            val published = modelStore.publishIfValid(spec, temp)
            setProgress(workDataOf(KEY_PROGRESS to 1f))
            Result.success(workDataOf(KEY_PATH to published.absolutePath))
        } catch (t: Throwable) {
            modelStore.deleteCorrupt(spec)
            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                Result.failure(workDataOf(KEY_ERROR to (t.message ?: "Download failed")))
            }
        }
    }

    private suspend fun copyFromAssets(assetPath: String, dest: java.io.File) {
        applicationContext.assets.open(assetPath).use { input ->
            val total = input.available().toLong().coerceAtLeast(1L)
            FileOutputStream(dest).use { output ->
                val buffer = ByteArray(DEFAULT_BUFFER)
                var copied = 0L
                var lastProgressEmit = 0L
                while (true) {
                    val read = input.read(buffer)
                    if (read < 0) break
                    output.write(buffer, 0, read)
                    copied += read
                    val now = System.currentTimeMillis()
                    if (now - lastProgressEmit >= PROGRESS_THROTTLE_MS) {
                        setProgress(workDataOf(KEY_PROGRESS to (copied.toFloat() / total).coerceIn(0f, 1f)))
                        lastProgressEmit = now
                    }
                }
                output.flush()
            }
        }
        setProgress(workDataOf(KEY_PROGRESS to 1f))
    }

    private suspend fun downloadHttps(url: String, dest: java.io.File, expectedSize: Long) {
        val request = Request.Builder().url(url).get().build()
        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                error("HTTP ${response.code} downloading $url")
            }
            val body = response.body
            val total = when {
                body.contentLength() > 0 -> body.contentLength()
                expectedSize > 0 -> expectedSize
                else -> -1L
            }
            body.byteStream().use { input ->
                FileOutputStream(dest).use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER)
                    var copied = 0L
                    var lastProgressEmit = 0L
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        output.write(buffer, 0, read)
                        copied += read
                        val now = System.currentTimeMillis()
                        if (now - lastProgressEmit >= PROGRESS_THROTTLE_MS) {
                            val progress = if (total > 0) {
                                (copied.toFloat() / total).coerceIn(0f, 1f)
                            } else {
                                0f
                            }
                            setProgress(workDataOf(KEY_PROGRESS to progress))
                            lastProgressEmit = now
                        }
                    }
                    output.flush()
                }
            }
        }
        setProgress(workDataOf(KEY_PROGRESS to 1f))
    }

    companion object {
        const val KEY_MODEL_ID = "model_id"
        const val KEY_VERSION = "version"
        const val KEY_PROGRESS = "progress"
        const val KEY_PATH = "path"
        const val KEY_ERROR = "error"

        private const val ASSET_SCHEME = "asset://"
        private const val DEFAULT_BUFFER = 64 * 1024
        private const val PROGRESS_THROTTLE_MS = 150L
        private const val MAX_RETRIES = 3

        fun uniqueWorkName(modelId: String, version: String): String =
            "model-download-$modelId-$version"

        fun defaultOkHttpClient(): OkHttpClient =
            OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.MINUTES)
                .build()
    }
}
