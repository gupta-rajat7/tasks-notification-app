package com.guptarajat.screenactivetaskreminder.settings

const val DEFAULT_REMINDER_INTERVAL_MINUTES = 10
const val DEFAULT_SNOOZE_MINUTES = 30
const val MIN_REMINDER_INTERVAL_MINUTES = 5
const val MAX_REMINDER_INTERVAL_MINUTES = 120
const val MIN_SNOOZE_MINUTES = 5
const val MAX_SNOOZE_MINUTES = 240
const val MINUTES_PER_DAY = 24 * 60
const val DEFAULT_QUIET_HOURS_START_MINUTE = 22 * 60
const val DEFAULT_QUIET_HOURS_END_MINUTE = 7 * 60
const val QUIET_HOURS_STEP_MINUTES = 60

data class TaskReminderSettings(
    val reminderIntervalMinutes: Int = DEFAULT_REMINDER_INTERVAL_MINUTES,
    val snoozeMinutes: Int = DEFAULT_SNOOZE_MINUTES,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStartMinuteOfDay: Int = DEFAULT_QUIET_HOURS_START_MINUTE,
    val quietHoursEndMinuteOfDay: Int = DEFAULT_QUIET_HOURS_END_MINUTE,
    val lastReviewedAtMillis: Long? = null,
    val snoozedUntilMillis: Long? = null,
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

fun normalizeMinuteOfDay(value: Int): Int =
    ((value % MINUTES_PER_DAY) + MINUTES_PER_DAY) % MINUTES_PER_DAY

fun formatMinuteOfDay(value: Int): String {
    val normalizedValue = normalizeMinuteOfDay(value)
    val hour = normalizedValue / 60
    val minute = normalizedValue % 60
    return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}
