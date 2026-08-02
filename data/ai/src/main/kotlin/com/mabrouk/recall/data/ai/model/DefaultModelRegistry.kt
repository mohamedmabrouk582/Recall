package com.mabrouk.recall.data.ai.model

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Built-in registry. Ships a tiny float sine model as plumbing for #3;
 * real embedding / LLM packs register here later (#8, #10).
 *
 * SHA-256 is of `assets/models/placeholder_add.tflite`
 * (TensorFlow Lite Micro hello_world_float).
 */
@Singleton
class DefaultModelRegistry @Inject constructor() : ModelRegistry {

    private val models: List<ModelSpec> = listOf(
        ModelSpec(
            id = ModelIds.PLACEHOLDER_SINE,
            version = "1.0.0",
            sha256 = PLACEHOLDER_SHA256,
            uri = "asset://models/placeholder_add.tflite",
            sizeBytes = PLACEHOLDER_SIZE_BYTES,
            displayName = "Placeholder sine (LiteRT plumbing)",
        ),
    )

    private val byId = models.associateBy { it.id }

    override fun get(id: String): ModelSpec? = byId[id]

    override fun all(): List<ModelSpec> = models

    companion object {
        const val PLACEHOLDER_SHA256 =
            "ee939863195ca37ce063b18e14fb82aa0d98db6596ba41095757f6b560da1070"
        const val PLACEHOLDER_SIZE_BYTES = 3164L
    }
}
