package com.example.taskreminder.settings

const val DEFAULT_REMINDER_INTERVAL_MINUTES = 10
const val DEFAULT_SNOOZE_MINUTES = 30
const val MIN_REMINDER_INTERVAL_MINUTES = 5
const val MAX_REMINDER_INTERVAL_MINUTES = 120
const val MIN_SNOOZE_MINUTES = 5
const val MAX_SNOOZE_MINUTES = 240

data class TaskReminderSettings(
    val reminderIntervalMinutes: Int = DEFAULT_REMINDER_INTERVAL_MINUTES,
    val snoozeMinutes: Int = DEFAULT_SNOOZE_MINUTES,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

enum class ThemeMode(
    val storageValue: String,
    val label: String,
) {
    SYSTEM("system", "System"),
    LIGHT("light", "Light"),
    DARK("dark", "Dark");

    companion object {
        fun fromStorageValue(value: String?): ThemeMode =
            entries.firstOrNull { it.storageValue == value } ?: SYSTEM
    }
}

fun clampReminderIntervalMinutes(value: Int): Int =
    value.coerceIn(MIN_REMINDER_INTERVAL_MINUTES, MAX_REMINDER_INTERVAL_MINUTES)

fun clampSnoozeMinutes(value: Int): Int =
    value.coerceIn(MIN_SNOOZE_MINUTES, MAX_SNOOZE_MINUTES)
