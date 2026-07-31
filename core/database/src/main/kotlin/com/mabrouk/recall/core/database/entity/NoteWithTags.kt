package com.mabrouk.recall.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithTags(
    @Embedded val note: NoteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "note_id",
        entity = NoteTagEntity::class,
    )
    val tags: List<NoteTagEntity>,
)
