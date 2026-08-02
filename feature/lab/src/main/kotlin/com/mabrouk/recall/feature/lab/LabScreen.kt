package com.mabrouk.recall.feature.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mabrouk.recall.data.ai.model.ModelAccelerator
import com.mabrouk.recall.data.ai.model.ModelStatus

@Composable
fun LabScreen(
    cloudAiEnabled: Boolean,
    onCloudAiEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LabViewModel = hiltViewModel(),
) {
    val modelStatus by viewModel.modelStatus.collectAsStateWithLifecycle()
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = "AI Lab", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Benches, bakeoffs, RAG traces, and eval scores will live here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        ElevatedCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.padding(end = 16.dp)) {
                    Text(
                        text = "Cloud AI",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Feature flag (persisted via DataStore)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = cloudAiEnabled,
                    onCheckedChange = onCloudAiEnabledChange,
                )
            }
        }

        ElevatedCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "On-device model",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = viewModel.modelId,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = statusLabel(modelStatus),
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (modelStatus is ModelStatus.Downloading) {
                    LinearProgressIndicator(
                        progress = { (modelStatus as ModelStatus.Downloading).progress },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                ui.message?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                ui.smokeResult?.let { result ->
                    Text(
                        text = "in=${result.inputPreview} out=${result.outputPreview}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.ensureModel(ModelAccelerator.CPU) },
                        enabled = !ui.busy,
                    ) {
                        Text("Ensure model")
                    }
                    OutlinedButton(
                        onClick = viewModel::runSmokeInference,
                        enabled = !ui.busy && modelStatus is ModelStatus.Ready,
                    ) {
                        Text("Smoke inference")
                    }
                }
            }
        }
    }
}

private fun statusLabel(status: ModelStatus): String = when (status) {
    ModelStatus.NotDownloaded -> "Status: not downloaded"
    is ModelStatus.Downloading -> "Status: downloading ${(status.progress * 100).toInt()}%"
    ModelStatus.Verifying -> "Status: verifying"
    is ModelStatus.Ready ->
        "Status: ready (${status.accelerator}, cold ${status.coldLoadMs}ms)"
    is ModelStatus.Error -> "Status: error — ${status.message}"
}
