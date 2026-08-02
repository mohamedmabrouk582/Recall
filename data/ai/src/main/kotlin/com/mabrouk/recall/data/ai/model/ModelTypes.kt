package com.mabrouk.recall.data.ai.model

/**
 * Declares a downloadable on-device model (versioned + integrity-checked).
 */
data class ModelSpec(
    val id: String,
    val version: String,
    val sha256: String,
    val uri: String,
    val sizeBytes: Long,
    val displayName: String,
)

enum class ModelAccelerator {
    CPU,
    GPU,
    NPU,
}

sealed class ModelStatus {
    data object NotDownloaded : ModelStatus()
    data class Downloading(val progress: Float) : ModelStatus()
    data object Verifying : ModelStatus()
    data class Ready(
        val path: String,
        val accelerator: ModelAccelerator,
        val coldLoadMs: Long,
    ) : ModelStatus()
    data class Error(
        val message: String,
        val cause: Throwable? = null,
    ) : ModelStatus()
}

data class SmokeInferenceResult(
    val inputPreview: String,
    val outputPreview: String,
    val latencyMs: Long,
)
