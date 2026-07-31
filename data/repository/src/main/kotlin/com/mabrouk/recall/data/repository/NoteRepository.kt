package com.mabrouk.recall.data.repository

import com.mabrouk.recall.core.model.Note
import com.mabrouk.recall.core.model.NoteId
import com.mabrouk.recall.core.model.SourceType
import kotlinx.coroutines.flow.Flow

/**
 * Sole gateway for note persistence. Features must not touch Room DAOs.
 */
interface NoteRepository {
    suspend fun create(
        title: String,
        body: String,
        sourceType: SourceType,
        tags: List<String> = emptyList(),
    ): Note

    /** Updates an existing note; [Note.createdAt] and [Note.id] are preserved as provided. */
    suspend fun update(note: Note)

    suspend fun get(id: NoteId): Note?

    suspend fun delete(id: NoteId)

    fun observeAll(): Flow<List<Note>>

    fun observe(id: NoteId): Flow<Note?>
}
