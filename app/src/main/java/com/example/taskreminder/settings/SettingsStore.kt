package com.example.taskreminder.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val SETTINGS_STORE_NAME = "task_reminder_settings"

private val Context.taskReminderSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_STORE_NAME,
)

class SettingsStore(context: Context) {
    private val dataStore = context.applicationContext.taskReminderSettingsDataStore

    val settings: Flow<TaskReminderSettings> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw error
            }
        }
        .map { preferences ->
            TaskReminderSettings(
                reminderIntervalMinutes = clampReminderIntervalMinutes(
                    preferences[ReminderIntervalMinutesKey] ?: DEFAULT_REMINDER_INTERVAL_MINUTES,
                ),
                snoozeMinutes = clampSnoozeMinutes(
                    preferences[SnoozeMinutesKey] ?: DEFAULT_SNOOZE_MINUTES,
                ),
                themeMode = ThemeMode.fromStorageValue(preferences[ThemeModeKey]),
            )
        }

    suspend fun setReminderIntervalMinutes(value: Int) {
        dataStore.edit { preferences ->
            preferences[ReminderIntervalMinutesKey] = clampReminderIntervalMinutes(value)
        }
    }

    suspend fun setSnoozeMinutes(value: Int) {
        dataStore.edit { preferences ->
            preferences[SnoozeMinutesKey] = clampSnoozeMinutes(value)
        }
    }

    suspend fun setThemeMode(value: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[ThemeModeKey] = value.storageValue
        }
    }

    private companion object {
        val ReminderIntervalMinutesKey = intPreferencesKey("reminder_interval_minutes")
        val SnoozeMinutesKey = intPreferencesKey("snooze_minutes")
        val ThemeModeKey = stringPreferencesKey("theme_mode")
    }
}
