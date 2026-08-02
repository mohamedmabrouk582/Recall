package com.mabrouk.recall.feature.lab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mabrouk.recall.data.ai.DefaultModelManager
import com.mabrouk.recall.data.ai.ModelManager
import com.mabrouk.recall.data.ai.model.ModelAccelerator
import com.mabrouk.recall.data.ai.model.ModelStatus
import com.mabrouk.recall.data.ai.model.SmokeInferenceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LabModelUiState(
    val status: ModelStatus = ModelStatus.NotDownloaded,
    val smokeResult: SmokeInferenceResult? = null,
    val busy: Boolean = false,
    val message: String? = null,
)

@HiltViewModel
class LabViewModel @Inject constructor(
    private val modelManager: ModelManager,
) : ViewModel() {

    val modelId: String = DefaultModelManager.DefaultModelId

    val modelStatus: StateFlow<ModelStatus> = modelManager.observe(modelId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ModelStatus.NotDownloaded)

    private val _ui = MutableStateFlow(LabModelUiState())
    val ui: StateFlow<LabModelUiState> = _ui.asStateFlow()

    fun ensureModel(preferred: ModelAccelerator = ModelAccelerator.CPU) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(busy = true, message = null, smokeResult = null)
            val status = modelManager.ensureModel(modelId, preferred)
            val message = when (status) {
                is ModelStatus.Ready ->
                    "Ready on ${status.accelerator} (${status.coldLoadMs}ms cold load)"
                is ModelStatus.Error -> status.message
                else -> status.toString()
            }
            _ui.value = _ui.value.copy(busy = false, message = message)
        }
    }

    fun runSmokeInference() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(busy = true, message = null)
            try {
                val result = modelManager.runSmokeInference(modelId)
                _ui.value = _ui.value.copy(
                    busy = false,
                    smokeResult = result,
                    message = "Smoke OK in ${result.latencyMs}ms",
                )
            } catch (t: Throwable) {
                _ui.value = _ui.value.copy(
                    busy = false,
                    message = t.message ?: "Smoke inference failed",
                )
            }
        }
    }
}
