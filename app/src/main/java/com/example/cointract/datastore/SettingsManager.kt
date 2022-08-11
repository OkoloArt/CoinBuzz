package com.example.cointract.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val LAYOUT_PREFERENCES_NAME = "layout_preferences"

// Create a DataStore instance using the preferencesDataStore delegate, with the Context as
// receiver.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCES_NAME
)

class SettingsManager(context: Context) {

    // Store user launch screen preference
    // refer to the data store and using edit
    // we can store values using the keys
    suspend fun storeUserLaunchScreen(launchScreen: String, context: Context) {
        context.dataStore.edit {
            it[LAUNCH_SCREEN] = launchScreen
        }
    }

    suspend fun storeUserDayNightTheme(dayNight: Boolean, context: Context) {
        context.dataStore.edit {
            it[DAY_NIGHT_MODE] = dayNight
        }
    }

    suspend fun storeUserBiometricSettings(biometric: Boolean, context: Context) {
        context.dataStore.edit {
            it[BIOMETRIC_SETTING] = biometric
        }
    }

    val preferenceLaunchScreenFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            // On the first run of the app, we will use 0 by default
            preferences[LAUNCH_SCREEN] ?: "Markets"
        }

    val preferenceDayNightFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DAY_NIGHT_MODE] ?: false
        }

    val preferenceBiometricFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[BIOMETRIC_SETTING] ?: false
        }

    companion object {
        private val LAUNCH_SCREEN = stringPreferencesKey("launch_screen")
        private val DAY_NIGHT_MODE = booleanPreferencesKey("day_night_mode")
        private val BIOMETRIC_SETTING = booleanPreferencesKey("biometric")
    }
}