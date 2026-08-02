package com.mabrouk.recall.data.ai.runtime

import android.util.Log
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.CompiledModel
import com.mabrouk.recall.data.ai.model.ModelAccelerator
import com.mabrouk.recall.data.ai.model.SmokeInferenceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureTimeMillis

data class LoadedModel(
    val path: String,
    val accelerator: ModelAccelerator,
    val coldLoadMs: Long,
    val compiledModel: CompiledModel,
)

/**
 * Loads verified `.tflite` files into LiteRT [CompiledModel].
 * Prefers the requested accelerator and falls back to CPU.
 */
@Singleton
class LiteRtModelLoader @Inject constructor() {

    private val mutex = Mutex()
    private val cache = mutableMapOf<String, LoadedModel>()

    suspend fun load(
        modelId: String,
        path: String,
        preferred: ModelAccelerator,
    ): LoadedModel = withContext(Dispatchers.Default) {
        mutex.withLock {
            cache[modelId]?.takeIf { it.path == path && it.accelerator == preferred }?.let {
                return@withContext it
            }
            cache.remove(modelId)?.compiledModel?.closeQuietly()

            val order = acceleratorOrder(preferred)
            var lastError: Throwable? = null
            for (accelerator in order) {
                try {
                    lateinit var compiled: CompiledModel
                    val coldLoadMs = measureTimeMillis {
                        compiled = CompiledModel.create(
                            path,
                            CompiledModel.Options(accelerator.toLiteRt()),
                        )
                    }
                    Log.i(TAG, "Loaded $modelId on $accelerator in ${coldLoadMs}ms")
                    val loaded = LoadedModel(
                        path = path,
                        accelerator = accelerator,
                        coldLoadMs = coldLoadMs,
                        compiledModel = compiled,
                    )
                    cache[modelId] = loaded
                    return@withContext loaded
                } catch (t: Throwable) {
                    lastError = t
                    Log.w(TAG, "Failed to load $modelId on $accelerator: ${t.message}")
                }
            }
            throw IllegalStateException(
                "Unable to load model $modelId with any accelerator",
                lastError,
            )
        }
    }

    suspend fun getCached(modelId: String): LoadedModel? = mutex.withLock { cache[modelId] }

    suspend fun runSmokeInference(modelId: String): SmokeInferenceResult =
        withContext(Dispatchers.Default) {
            val loaded = mutex.withLock {
                cache[modelId] ?: error("Model $modelId is not loaded")
            }
            val model = loaded.compiledModel
            val inputs = model.createInputBuffers()
            val outputs = model.createOutputBuffers()
            try {
                val sample = floatArrayOf(1.0f)
                inputs[0].writeFloat(sample)
                val latencyMs = measureTimeMillis {
                    model.run(inputs, outputs)
                }
                val out = outputs[0].readFloat()
                SmokeInferenceResult(
                    inputPreview = sample.contentToString(),
                    outputPreview = out.contentToString(),
                    latencyMs = latencyMs,
                )
            } finally {
                inputs.forEach { it.closeQuietly() }
                outputs.forEach { it.closeQuietly() }
            }
        }

    suspend fun close(modelId: String) = mutex.withLock {
        cache.remove(modelId)?.compiledModel?.closeQuietly()
    }

    private fun acceleratorOrder(preferred: ModelAccelerator): List<ModelAccelerator> =
        when (preferred) {
            ModelAccelerator.CPU -> listOf(ModelAccelerator.CPU)
            ModelAccelerator.GPU -> listOf(ModelAccelerator.GPU, ModelAccelerator.CPU)
            ModelAccelerator.NPU -> listOf(
                ModelAccelerator.NPU,
                ModelAccelerator.GPU,
                ModelAccelerator.CPU,
            )
        }

    private fun ModelAccelerator.toLiteRt(): Accelerator = when (this) {
        ModelAccelerator.CPU -> Accelerator.CPU
        ModelAccelerator.GPU -> Accelerator.GPU
        ModelAccelerator.NPU -> Accelerator.NPU
    }

    private fun AutoCloseable.closeQuietly() {
        try {
            close()
        } catch (_: Throwable) {
            // Best-effort native cleanup.
        }
    }

    private companion object {
        const val TAG = "LiteRtModelLoader"
    }
}
