package dev.luma.appopenconfirmation.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MonitoredAppsRepository(
    private val dataStore: DataStore<Preferences>
) {
    val monitoredApps: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.MONITORED_APPS] ?: emptySet()
        }

    suspend fun addMonitoredApp(packageName: String) {
        dataStore.edit { preferences ->
            val currentApps = preferences[PreferencesKeys.MONITORED_APPS] ?: emptySet()
            preferences[PreferencesKeys.MONITORED_APPS] = currentApps + packageName
        }
    }

    suspend fun removeMonitoredApp(packageName: String) {
        dataStore.edit { preferences ->
            val currentApps = preferences[PreferencesKeys.MONITORED_APPS] ?: emptySet()
            preferences[PreferencesKeys.MONITORED_APPS] = currentApps - packageName
        }
    }

    suspend fun toggleMonitoredApp(packageName: String) {
        dataStore.edit { preferences ->
            val currentApps = preferences[PreferencesKeys.MONITORED_APPS] ?: emptySet()
            preferences[PreferencesKeys.MONITORED_APPS] = if (packageName in currentApps) {
                currentApps - packageName
            } else {
                currentApps + packageName
            }
        }
    }
}
