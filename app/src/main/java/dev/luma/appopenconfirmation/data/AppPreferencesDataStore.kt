package dev.luma.appopenconfirmation.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

object PreferencesKeys {
    val MONITORED_APPS = stringSetPreferencesKey("monitored_apps")
}
