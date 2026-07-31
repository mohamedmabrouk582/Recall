package com.mabrouk.recall.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "recall_settings")

/**
 * Minimal DataStore-backed feature-flag store. Later AI paths (e.g. cloud routing) can be
 * toggled at runtime without a rebuild. Provided by Hilt via constructor injection.
 */
@Singleton
class FeatureFlagStore @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    val cloudAiEnabled: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[CLOUD_AI_ENABLED] ?: false }

    suspend fun setCloudAiEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[CLOUD_AI_ENABLED] = enabled }
    }

    private companion object {
        val CLOUD_AI_ENABLED = booleanPreferencesKey("cloud_ai_enabled")
    }
}
