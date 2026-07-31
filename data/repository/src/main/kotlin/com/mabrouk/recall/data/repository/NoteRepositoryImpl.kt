package com.mabrouk.recall.data.repository

import com.mabrouk.recall.core.database.crypto.NoteCrypto
import com.mabrouk.recall.core.database.dao.NoteDao
import com.mabrouk.recall.core.database.entity.NoteEntity
import com.mabrouk.recall.core.database.entity.NoteTagEntity
import com.mabrouk.recall.core.database.entity.NoteWithTags
import com.mabrouk.recall.core.model.Note
import com.mabrouk.recall.core.model.NoteId
import com.mabrouk.recall.core.model.SourceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val noteCrypto: NoteCrypto,
) : NoteRepository {

    override suspend fun create(
        title: String,
        body: String,
        sourceType: SourceType,
        tags: List<String>,
    ): Note {
        val id = NoteId(UUID.randomUUID().toString())
        val createdAt = System.currentTimeMillis()
        val note = Note(
            id = id,
            title = title,
            body = body,
            createdAt = createdAt,
            sourceType = sourceType,
            tags = tags.distinct(),
        )
        noteDao.insertNoteWithTags(note.toEntity(), note.toTagEntities())
        return note
    }

    override suspend fun update(note: Note) {
        val normalized = note.copy(tags = note.tags.distinct())
        noteDao.updateNoteWithTags(normalized.toEntity(), normalized.toTagEntities())
    }

    override suspend fun get(id: NoteId): Note? =
        noteDao.getById(id.value)?.toDomain()

    override suspend fun delete(id: NoteId) {
        noteDao.deleteNoteById(id.value)
    }

    override fun observeAll(): Flow<List<Note>> =
        noteDao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override fun observe(id: NoteId): Flow<Note?> =
        noteDao.observeById(id.value).map { it?.toDomain() }

    private fun Note.toEntity(): NoteEntity = NoteEntity(
        id = id.value,
        titleCiphertext = noteCrypto.encrypt(title),
        bodyCiphertext = noteCrypto.encrypt(body),
        createdAt = createdAt,
        sourceType = sourceType.name,
    )

    private fun Note.toTagEntities(): List<NoteTagEntity> =
        tags.map { NoteTagEntity(noteId = id.value, tag = it) }

    private fun NoteWithTags.toDomain(): Note = Note(
        id = NoteId(note.id),
        title = noteCrypto.decrypt(note.titleCiphertext),
        body = noteCrypto.decrypt(note.bodyCiphertext),
        createdAt = note.createdAt,
        sourceType = SourceType.valueOf(note.sourceType),
        tags = tags.map { it.tag },
    )
}
