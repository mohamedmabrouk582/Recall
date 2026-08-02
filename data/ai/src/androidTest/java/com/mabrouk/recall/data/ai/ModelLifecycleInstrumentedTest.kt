package com.mabrouk.recall.data.ai

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.common.truth.Truth.assertThat
import com.mabrouk.recall.data.ai.download.ModelDownloadWorker
import com.mabrouk.recall.data.ai.model.DefaultModelRegistry
import com.mabrouk.recall.data.ai.model.ModelAccelerator
import com.mabrouk.recall.data.ai.model.ModelIds
import com.mabrouk.recall.data.ai.model.ModelStatus
import com.mabrouk.recall.data.ai.runtime.LiteRtModelLoader
import com.mabrouk.recall.data.ai.store.ModelStore
import com.mabrouk.recall.data.ai.store.Sha256Verifier
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ModelLifecycleInstrumentedTest {

    private lateinit var manager: DefaultModelManager
    private lateinit var store: ModelStore

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .setWorkerFactory(
                object : androidx.work.WorkerFactory() {
                    override fun createWorker(
                        appContext: android.content.Context,
                        workerClassName: String,
                        workerParameters: androidx.work.WorkerParameters,
                    ): androidx.work.ListenableWorker? {
                        if (workerClassName == ModelDownloadWorker::class.java.name) {
                            return ModelDownloadWorker(
                                appContext,
                                workerParameters,
                                DefaultModelRegistry(),
                                ModelStore(appContext, Sha256Verifier()),
                                ModelDownloadWorker.defaultOkHttpClient(),
                            )
                        }
                        return null
                    }
                },
            )
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        val registry = DefaultModelRegistry()
        store = ModelStore(context, Sha256Verifier())
        val spec = registry.get(ModelIds.PLACEHOLDER_SINE)!!
        store.deleteCorrupt(spec)

        manager = DefaultModelManager(
            context = context,
            registry = registry,
            modelStore = store,
            loader = LiteRtModelLoader(),
        )
    }

    @Test
    fun ensureModel_downloadsVerifiesAndRunsSmokeInference() = runBlocking {
        val status = manager.ensureModel(ModelIds.PLACEHOLDER_SINE, ModelAccelerator.CPU)
        assertThat(status).isInstanceOf(ModelStatus.Ready::class.java)
        val ready = status as ModelStatus.Ready
        assertThat(ready.accelerator).isEqualTo(ModelAccelerator.CPU)
        assertThat(ready.coldLoadMs).isAtLeast(0)

        val smoke = manager.runSmokeInference(ModelIds.PLACEHOLDER_SINE)
        assertThat(smoke.outputPreview).isNotEmpty()
        assertThat(smoke.latencyMs).isAtLeast(0)
    }

    @Test
    fun corruptFile_isRejectedThenRecovered() = runBlocking {
        val registry = DefaultModelRegistry()
        val spec = registry.get(ModelIds.PLACEHOLDER_SINE)!!

        // First ensure creates a valid file.
        val first = manager.ensureModel(ModelIds.PLACEHOLDER_SINE, ModelAccelerator.CPU)
        assertThat(first).isInstanceOf(ModelStatus.Ready::class.java)

        // Corrupt on disk.
        store.modelFile(spec).writeBytes(byteArrayOf(1, 2, 3, 4, 5))
        assertThat(store.existsAndValid(spec)).isFalse()

        val recovered = manager.ensureModel(ModelIds.PLACEHOLDER_SINE, ModelAccelerator.CPU)
        assertThat(recovered).isInstanceOf(ModelStatus.Ready::class.java)
        assertThat(store.existsAndValid(spec)).isTrue()
    }
}
