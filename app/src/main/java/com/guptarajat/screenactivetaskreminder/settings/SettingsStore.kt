package com.guptarajat.screenactivetaskreminder.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
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
                hasCompletedOnboarding = preferences[OnboardingCompletedKey] ?: false,
                quietHoursEnabled = preferences[QuietHoursEnabledKey] ?: false,
                quietHoursStartMinuteOfDay = normalizeMinuteOfDay(
                    preferences[QuietHoursStartMinuteOfDayKey] ?: DEFAULT_QUIET_HOURS_START_MINUTE,
                ),
                quietHoursEndMinuteOfDay = normalizeMinuteOfDay(
                    preferences[QuietHoursEndMinuteOfDayKey] ?: DEFAULT_QUIET_HOURS_END_MINUTE,
                ),
                lastReviewedAtMillis = preferences[LastReviewedAtMillisKey],
                snoozedUntilMillis = preferences[SnoozedUntilMillisKey],
                screenActivityModeEnabled = preferences[ScreenActivityModeEnabledKey] ?: false,
            )
        }

    suspend fun setOnboardingCompleted(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[OnboardingCompletedKey] = value
        }
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

    suspend fun setScreenActivityModeEnabled(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[ScreenActivityModeEnabledKey] = value
        }
    }

    suspend fun setQuietHoursEnabled(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[QuietHoursEnabledKey] = value
        }
    }

    suspend fun setQuietHoursStartMinuteOfDay(value: Int) {
        dataStore.edit { preferences ->
            preferences[QuietHoursStartMinuteOfDayKey] = normalizeMinuteOfDay(value)
        }
    }

    suspend fun setQuietHoursEndMinuteOfDay(value: Int) {
        dataStore.edit { preferences ->
            preferences[QuietHoursEndMinuteOfDayKey] = normalizeMinuteOfDay(value)
        }
    }

    suspend fun recordReview(nowMillis: Long) {
        dataStore.edit { preferences ->
            preferences[LastReviewedAtMillisKey] = nowMillis
            preferences.remove(SnoozedUntilMillisKey)
        }
    }

    suspend fun snoozeUntil(untilMillis: Long) {
        dataStore.edit { preferences ->
            preferences[SnoozedUntilMillisKey] = untilMillis
        }
    }

    private companion object {
        val ReminderIntervalMinutesKey = intPreferencesKey("reminder_interval_minutes")
        val SnoozeMinutesKey = intPreferencesKey("snooze_minutes")
        val ThemeModeKey = stringPreferencesKey("theme_mode")
        val OnboardingCompletedKey = booleanPreferencesKey("onboarding_completed")
        val QuietHoursEnabledKey = booleanPreferencesKey("quiet_hours_enabled")
        val QuietHoursStartMinuteOfDayKey = intPreferencesKey("quiet_hours_start_minute_of_day")
        val QuietHoursEndMinuteOfDayKey = intPreferencesKey("quiet_hours_end_minute_of_day")
        val LastReviewedAtMillisKey = longPreferencesKey("last_reviewed_at_millis")
        val SnoozedUntilMillisKey = longPreferencesKey("snoozed_until_millis")
        val ScreenActivityModeEnabledKey = booleanPreferencesKey("screen_activity_mode_enabled")
    }
}
