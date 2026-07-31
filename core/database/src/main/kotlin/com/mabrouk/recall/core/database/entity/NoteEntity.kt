package com.mabrouk.recall.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    indices = [Index(value = ["created_at"])],
)
data class NoteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title_ciphertext")
    val titleCiphertext: ByteArray,
    @ColumnInfo(name = "body_ciphertext")
    val bodyCiphertext: ByteArray,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "source_type")
    val sourceType: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as NoteEntity
        return id == other.id &&
            titleCiphertext.contentEquals(other.titleCiphertext) &&
            bodyCiphertext.contentEquals(other.bodyCiphertext) &&
            createdAt == other.createdAt &&
            sourceType == other.sourceType
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + titleCiphertext.contentHashCode()
        result = 31 * result + bodyCiphertext.contentHashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + sourceType.hashCode()
        return result
    }
}

@Entity(
    tableName = "note_tags",
    primaryKeys = ["note_id", "tag"],
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["tag"]), Index(value = ["note_id"])],
)
data class NoteTagEntity(
    @ColumnInfo(name = "note_id")
    val noteId: String,
    @ColumnInfo(name = "tag")
    val tag: String,
)
