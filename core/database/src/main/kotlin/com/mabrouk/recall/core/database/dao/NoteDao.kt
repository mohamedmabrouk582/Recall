package com.mabrouk.recall.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mabrouk.recall.core.database.entity.NoteEntity
import com.mabrouk.recall.core.database.entity.NoteTagEntity
import com.mabrouk.recall.core.database.entity.NoteWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<NoteTagEntity>)

    @Query("DELETE FROM note_tags WHERE note_id = :noteId")
    suspend fun deleteTagsForNote(noteId: String)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: String)

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getById(noteId: String): NoteWithTags?

    @Transaction
    @Query("SELECT * FROM notes ORDER BY created_at DESC")
    fun observeAll(): Flow<List<NoteWithTags>>

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun observeById(noteId: String): Flow<NoteWithTags?>

    @Transaction
    suspend fun insertNoteWithTags(note: NoteEntity, tags: List<NoteTagEntity>) {
        insertNote(note)
        if (tags.isNotEmpty()) {
            insertTags(tags)
        }
    }

    @Transaction
    suspend fun updateNoteWithTags(note: NoteEntity, tags: List<NoteTagEntity>) {
        updateNote(note)
        deleteTagsForNote(note.id)
        if (tags.isNotEmpty()) {
            insertTags(tags)
        }
    }
}
