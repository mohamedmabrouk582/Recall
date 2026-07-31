package com.mabrouk.recall.feature.ask

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mabrouk.recall.core.designsystem.component.PlaceholderScreen

@Composable
fun AskScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        title = "Ask",
        subtitle = "Ask questions grounded in your own notes (RAG).",
        modifier = modifier,
    )
}
