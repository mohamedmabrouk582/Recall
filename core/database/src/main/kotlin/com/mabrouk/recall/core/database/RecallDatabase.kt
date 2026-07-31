package com.mabrouk.recall.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mabrouk.recall.core.database.dao.NoteDao
import com.mabrouk.recall.core.database.entity.NoteEntity
import com.mabrouk.recall.core.database.entity.NoteTagEntity

/**
 * Local persistence for notes. Schema v1 is minimal; pre-release we allow
 * destructive migration rather than hand-written Migration objects.
 */
@Database(
    entities = [NoteEntity::class, NoteTagEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class RecallDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        const val DATABASE_NAME = "recall.db"
    }
}
