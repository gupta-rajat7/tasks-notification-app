package com.example.taskreminder.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class TaskReminderSettingsTest {
    @Test
    fun defaultsMatchProductRequirements() {
        val settings = TaskReminderSettings()

        assertEquals(10, settings.reminderIntervalMinutes)
        assertEquals(30, settings.snoozeMinutes)
        assertEquals(ThemeMode.SYSTEM, settings.themeMode)
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
}
