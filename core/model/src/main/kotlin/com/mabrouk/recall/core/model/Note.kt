package com.mabrouk.recall.core.model

/**
 * Domain note — plaintext only, no Android / Room dependencies.
 *
 * [createdAt] is epoch millis (UTC).
 */
data class Note(
    val id: NoteId,
    val title: String,
    val body: String,
    val createdAt: Long,
    val sourceType: SourceType,
    val tags: List<String> = emptyList(),
)
