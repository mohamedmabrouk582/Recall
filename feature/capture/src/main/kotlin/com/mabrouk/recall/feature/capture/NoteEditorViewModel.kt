package com.mabrouk.recall.feature.capture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mabrouk.recall.core.model.Note
import com.mabrouk.recall.core.model.NoteId
import com.mabrouk.recall.core.model.SourceType
import com.mabrouk.recall.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteEditorUiState(
    val title: String = "",
    val body: String = "",
    val loading: Boolean = false,
    val saving: Boolean = false,
    val error: String? = null,
    val isNew: Boolean = true,
)

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
) : ViewModel() {

    private val _ui = MutableStateFlow(NoteEditorUiState())
    val ui: StateFlow<NoteEditorUiState> = _ui.asStateFlow()

    private val _saved = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val saved: SharedFlow<Unit> = _saved.asSharedFlow()

    private var existing: Note? = null
    private var loadedKey: String? = null

    fun load(noteId: String?) {
        if (noteId == null) {
            loadedKey = NEW_KEY
            existing = null
            _ui.value = NoteEditorUiState(isNew = true)
            return
        }

        val key = noteId
        if (loadedKey == key) return
        loadedKey = key

        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null, isNew = false) }
            val note = noteRepository.get(NoteId(noteId))
            if (note == null) {
                _ui.update {
                    it.copy(loading = false, error = "Note not found")
                }
            } else {
                existing = note
                _ui.update {
                    it.copy(
                        loading = false,
                        title = note.title,
                        body = note.body,
                        isNew = false,
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) {
        _ui.update { it.copy(title = value, error = null) }
    }

    fun onBodyChange(value: String) {
        _ui.update { it.copy(body = value, error = null) }
    }

    fun save() {
        val state = _ui.value
        val title = state.title.trim()
        val body = state.body.trim()
        if (body.isEmpty()) {
            _ui.update { it.copy(error = "Body is required") }
            return
        }
        if (state.saving) return

        viewModelScope.launch {
            _ui.update { it.copy(saving = true, error = null) }
            try {
                val current = existing
                if (current == null) {
                    existing = noteRepository.create(
                        title = title,
                        body = body,
                        sourceType = SourceType.TEXT,
                    )
                } else {
                    val updated = current.copy(title = title, body = body)
                    noteRepository.update(updated)
                    existing = updated
                }
                _ui.update { it.copy(saving = false, isNew = false) }
                _saved.emit(Unit)
            } catch (t: Throwable) {
                _ui.update {
                    it.copy(saving = false, error = t.message ?: "Could not save note")
                }
            }
        }
    }

    private companion object {
        const val NEW_KEY = "__new__"
    }
}
