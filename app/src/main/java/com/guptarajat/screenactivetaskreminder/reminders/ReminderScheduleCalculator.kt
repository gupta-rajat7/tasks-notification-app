package com.guptarajat.screenactivetaskreminder.reminders

import com.guptarajat.screenactivetaskreminder.settings.TaskReminderSettings

private const val MILLIS_PER_MINUTE = 60_000L
const val MIN_REMINDER_SCHEDULE_DELAY_MILLIS = MILLIS_PER_MINUTE

fun nextReminderDelayMillis(
    settings: TaskReminderSettings,
    nowMillis: Long,
    localMinuteOfDay: Int,
): Long {
    val configuredIntervalMillis =
        settings.reminderIntervalMinutes.coerceAtLeast(1) * MILLIS_PER_MINUTE
    val snoozeDelayMillis = settings.snoozedUntilMillis
        ?.let { it - nowMillis }
        ?.takeIf { it > 0L }
    val reviewDelayMillis = settings.lastReviewedAtMillis
        ?.let { reviewedAtMillis -> reviewedAtMillis + configuredIntervalMillis - nowMillis }
        ?.takeIf { it > 0L }
    val quietHoursDelayMillis = QuietHours(
        isEnabled = settings.quietHoursEnabled,
        startMinuteOfDay = settings.quietHoursStartMinuteOfDay,
        endMinuteOfDay = settings.quietHoursEndMinuteOfDay,
    ).minutesUntilEnd(localMinuteOfDay)
        ?.let { it * MILLIS_PER_MINUTE }

    val nextEligibleDelayMillis = listOfNotNull(
        snoozeDelayMillis,
        reviewDelayMillis,
        quietHoursDelayMillis,
    ).maxOrNull() ?: configuredIntervalMillis

    return nextEligibleDelayMillis.coerceAtLeast(MIN_REMINDER_SCHEDULE_DELAY_MILLIS)
}
