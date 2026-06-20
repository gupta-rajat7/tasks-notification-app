package com.guptarajat.screenactivetaskreminder.reminders

import com.guptarajat.screenactivetaskreminder.settings.TaskReminderSettings
import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderScheduleCalculatorTest {
    @Test
    fun usesConfiguredIntervalWhenNoSuppressionIsActive() {
        val delayMillis = nextReminderDelayMillis(
            settings = TaskReminderSettings(reminderIntervalMinutes = 10),
            nowMillis = 0L,
            localMinuteOfDay = 9 * 60,
        )

        assertEquals(10 * 60_000L, delayMillis)
    }

    @Test
    fun waitsUntilSnoozeExpires() {
        val delayMillis = nextReminderDelayMillis(
            settings = TaskReminderSettings(
                reminderIntervalMinutes = 10,
                snoozedUntilMillis = 30 * 60_000L,
            ),
            nowMillis = 0L,
            localMinuteOfDay = 9 * 60,
        )

        assertEquals(30 * 60_000L, delayMillis)
    }

    @Test
    fun waitsUntilReviewIntervalExpires() {
        val delayMillis = nextReminderDelayMillis(
            settings = TaskReminderSettings(
                reminderIntervalMinutes = 10,
                lastReviewedAtMillis = 0L,
            ),
            nowMillis = 5 * 60_000L,
            localMinuteOfDay = 9 * 60,
        )

        assertEquals(5 * 60_000L, delayMillis)
    }

    @Test
    fun waitsUntilSameDayQuietHoursEnd() {
        val delayMillis = nextReminderDelayMillis(
            settings = TaskReminderSettings(
                quietHoursEnabled = true,
                quietHoursStartMinuteOfDay = 13 * 60,
                quietHoursEndMinuteOfDay = 14 * 60,
            ),
            nowMillis = 0L,
            localMinuteOfDay = 13 * 60 + 30,
        )

        assertEquals(30 * 60_000L, delayMillis)
    }

    @Test
    fun waitsUntilOvernightQuietHoursEndBeforeMidnight() {
        val delayMillis = nextReminderDelayMillis(
            settings = TaskReminderSettings(
                quietHoursEnabled = true,
                quietHoursStartMinuteOfDay = 22 * 60,
                quietHoursEndMinuteOfDay = 7 * 60,
            ),
            nowMillis = 0L,
            localMinuteOfDay = 23 * 60,
        )

        assertEquals(8 * 60 * 60_000L, delayMillis)
    }

    @Test
    fun waitsUntilOvernightQuietHoursEndAfterMidnight() {
        val delayMillis = nextReminderDelayMillis(
            settings = TaskReminderSettings(
                quietHoursEnabled = true,
                quietHoursStartMinuteOfDay = 22 * 60,
                quietHoursEndMinuteOfDay = 7 * 60,
            ),
            nowMillis = 0L,
            localMinuteOfDay = 6 * 60 + 30,
        )

        assertEquals(30 * 60_000L, delayMillis)
    }

    @Test
    fun waitsForLatestActiveSuppression() {
        val delayMillis = nextReminderDelayMillis(
            settings = TaskReminderSettings(
                quietHoursEnabled = true,
                quietHoursStartMinuteOfDay = 22 * 60,
                quietHoursEndMinuteOfDay = 7 * 60,
                snoozedUntilMillis = 30 * 60_000L,
            ),
            nowMillis = 0L,
            localMinuteOfDay = 23 * 60,
        )

        assertEquals(8 * 60 * 60_000L, delayMillis)
    }

    @Test
    fun clampsToMinimumDelay() {
        val delayMillis = nextReminderDelayMillis(
            settings = TaskReminderSettings(
                reminderIntervalMinutes = 10,
                lastReviewedAtMillis = 0L,
            ),
            nowMillis = 10 * 60_000L - 500L,
            localMinuteOfDay = 9 * 60,
        )

        assertEquals(MIN_REMINDER_SCHEDULE_DELAY_MILLIS, delayMillis)
    }
}
