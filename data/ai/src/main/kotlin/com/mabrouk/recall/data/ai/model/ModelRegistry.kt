package com.mabrouk.recall.data.ai.model

/**
 * Catalog of on-device models. Entries are versioned and integrity-checked before load.
 */
interface ModelRegistry {
    fun get(id: String): ModelSpec?
    fun all(): List<ModelSpec>
}

object ModelIds {
    const val PLACEHOLDER_SINE = "placeholder_sine"
}
