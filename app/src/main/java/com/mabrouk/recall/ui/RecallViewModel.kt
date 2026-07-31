package com.mabrouk.recall.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mabrouk.recall.data.FeatureFlagStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecallViewModel @Inject constructor(
    private val featureFlags: FeatureFlagStore,
) : ViewModel() {

    val cloudAiEnabled: StateFlow<Boolean> = featureFlags.cloudAiEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun setCloudAiEnabled(enabled: Boolean) {
        viewModelScope.launch { featureFlags.setCloudAiEnabled(enabled) }
    }
}
