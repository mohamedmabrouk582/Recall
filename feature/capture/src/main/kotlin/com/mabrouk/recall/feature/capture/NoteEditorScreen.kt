package com.mabrouk.recall.feature.capture

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NoteEditorScreen(
    noteId: String?,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteEditorViewModel = hiltViewModel(key = noteId ?: "new"),
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    LaunchedEffect(noteId) {
        viewModel.load(noteId)
    }

    LaunchedEffect(viewModel) {
        viewModel.saved.collect { onSaved() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = if (ui.isNew) "New text note" else "Edit note",
            style = MaterialTheme.typography.headlineMedium,
        )

        when {
            ui.loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            else -> {
                OutlinedTextField(
                    value = ui.title,
                    onValueChange = viewModel::onTitleChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title (optional)") },
                    singleLine = true,
                    enabled = !ui.saving,
                )
                OutlinedTextField(
                    value = ui.body,
                    onValueChange = viewModel::onBodyChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Body") },
                    minLines = 8,
                    enabled = !ui.saving,
                )
                ui.error?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = viewModel::save,
                        enabled = !ui.saving,
                    ) {
                        Text(if (ui.saving) "Saving…" else "Save")
                    }
                    OutlinedButton(
                        onClick = onBack,
                        enabled = !ui.saving,
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
