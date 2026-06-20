package com.guptarajat.screenactivetaskreminder.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class TaskReminderSettingsTest {
    @Test
    fun defaultsMatchProductRequirements() {
        val settings = TaskReminderSettings()

        assertEquals(10, settings.reminderIntervalMinutes)
        assertEquals(30, settings.snoozeMinutes)
        assertEquals(ThemeMode.SYSTEM, settings.themeMode)
        assertEquals(false, settings.hasCompletedOnboarding)
        assertEquals(false, settings.quietHoursEnabled)
        assertEquals(22 * 60, settings.quietHoursStartMinuteOfDay)
        assertEquals(7 * 60, settings.quietHoursEndMinuteOfDay)
        assertEquals(null, settings.lastReviewedAtMillis)
        assertEquals(null, settings.snoozedUntilMillis)
    }

    @Test
    fun reminderIntervalClampsToSupportedRange() {
        assertEquals(MIN_REMINDER_INTERVAL_MINUTES, clampReminderIntervalMinutes(-1))
        assertEquals(15, clampReminderIntervalMinutes(15))
        assertEquals(MAX_REMINDER_INTERVAL_MINUTES, clampReminderIntervalMinutes(500))
    }

    @Test
    fun snoozeDurationClampsToSupportedRange() {
        assertEquals(MIN_SNOOZE_MINUTES, clampSnoozeMinutes(0))
        assertEquals(45, clampSnoozeMinutes(45))
        assertEquals(MAX_SNOOZE_MINUTES, clampSnoozeMinutes(500))
    }

    @Test
    fun unknownThemeValueFallsBackToSystem() {
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStorageValue("unexpected"))
    }

    @Test
    fun minuteOfDayWrapsAroundDayBoundaries() {
        assertEquals(23 * 60, normalizeMinuteOfDay(-60))
        assertEquals(0, normalizeMinuteOfDay(24 * 60))
        assertEquals(60, normalizeMinuteOfDay(25 * 60))
    }

    @Test
    fun minuteOfDayFormatsAsHourAndMinute() {
        assertEquals("00:00", formatMinuteOfDay(0))
        assertEquals("07:05", formatMinuteOfDay(7 * 60 + 5))
        assertEquals("23:00", formatMinuteOfDay(-60))
    }
}
