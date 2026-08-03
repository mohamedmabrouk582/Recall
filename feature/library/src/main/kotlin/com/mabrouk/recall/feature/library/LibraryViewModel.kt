package com.mabrouk.recall.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mabrouk.recall.core.model.Note
import com.mabrouk.recall.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class LibraryUiState(
    val notes: List<Note> = emptyList(),
    val isEmpty: Boolean = true,
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    noteRepository: NoteRepository,
) : ViewModel() {

    val ui: StateFlow<LibraryUiState> = noteRepository.observeAll()
        .map { notes -> LibraryUiState(notes = notes, isEmpty = notes.isEmpty()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LibraryUiState(),
        )
}
