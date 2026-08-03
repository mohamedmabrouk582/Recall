package com.mabrouk.recall.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Nested destinations pushed onto a top-level tab's back stack.
 *
 * [noteId] null means create a new text note; non-null opens an existing note.
 */
@Serializable
data class NoteEditorRoute(val noteId: String? = null) : NavKey
