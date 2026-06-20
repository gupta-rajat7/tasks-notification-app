package com.guptarajat.screenactivetaskreminder.reminders

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderRulesTest {
    @Test
    fun remindsWhenPendingTasksAreEligible() {
        val decision = ReminderRules.evaluate(
            ReminderRuleInput(
                pendingTaskCount = 3,
                nowMillis = 1_000L,
                localMinuteOfDay = 9 * 60,
                reminderIntervalMinutes = 10,
            ),
        )

        assertTrue(decision.shouldRemind)
        assertNull(decision.suppressionReason)
    }

    @Test
    fun suppressesWhenThereAreNoPendingTasks() {
        val decision = ReminderRules.evaluate(
            ReminderRuleInput(
                pendingTaskCount = 0,
                nowMillis = 1_000L,
                localMinuteOfDay = 9 * 60,
                reminderIntervalMinutes = 10,
            ),
        )

        assertFalse(decision.shouldRemind)
        assertEquals(ReminderSuppressionReason.NO_PENDING_TASKS, decision.suppressionReason)
    }

    @Test
    fun suppressesDuringSameDayQuietHours() {
        val quietHours = QuietHours(
            isEnabled = true,
            startMinuteOfDay = 13 * 60,
            endMinuteOfDay = 14 * 60,
        )

        val decision = ReminderRules.evaluate(
            ReminderRuleInput(
                pendingTaskCount = 1,
                nowMillis = 1_000L,
                localMinuteOfDay = 13 * 60 + 30,
                reminderIntervalMinutes = 10,
                quietHours = quietHours,
            ),
        )

        assertFalse(decision.shouldRemind)
        assertEquals(ReminderSuppressionReason.QUIET_HOURS, decision.suppressionReason)
    }

    @Test
    fun supportsOvernightQuietHours() {
        val quietHours = QuietHours(
            isEnabled = true,
            startMinuteOfDay = 22 * 60,
            endMinuteOfDay = 7 * 60,
        )

        assertTrue(quietHours.contains(23 * 60))
        assertTrue(quietHours.contains(6 * 60 + 59))
        assertFalse(quietHours.contains(12 * 60))
    }

    @Test
    fun ignoresQuietHoursWhenDisabled() {
        val decision = ReminderRules.evaluate(
            ReminderRuleInput(
                pendingTaskCount = 1,
                nowMillis = 1_000L,
                localMinuteOfDay = 23 * 60,
                reminderIntervalMinutes = 10,
                quietHours = QuietHours(isEnabled = false),
            ),
        )

        assertTrue(decision.shouldRemind)
    }

    @Test
    fun suppressesUntilSnoozeExpires() {
        val decision = ReminderRules.evaluate(
            ReminderRuleInput(
                pendingTaskCount = 1,
                nowMillis = 1_000L,
                localMinuteOfDay = 9 * 60,
                reminderIntervalMinutes = 10,
                snoozedUntilMillis = 2_000L,
            ),
        )

        assertFalse(decision.shouldRemind)
        assertEquals(ReminderSuppressionReason.SNOOZED, decision.suppressionReason)
        assertEquals(2_000L, decision.nextEligibleAtMillis)
    }

    @Test
    fun remindsAfterSnoozeExpires() {
        val decision = ReminderRules.evaluate(
            ReminderRuleInput(
                pendingTaskCount = 1,
                nowMillis = 2_000L,
                localMinuteOfDay = 9 * 60,
                reminderIntervalMinutes = 10,
                snoozedUntilMillis = 2_000L,
            ),
        )

        assertTrue(decision.shouldRemind)
    }

    @Test
    fun suppressesAfterRecentReviewUntilIntervalPasses() {
        val decision = ReminderRules.evaluate(
            ReminderRuleInput(
                pendingTaskCount = 1,
                nowMillis = 5 * 60_000L,
                localMinuteOfDay = 9 * 60,
                reminderIntervalMinutes = 10,
                lastReviewedAtMillis = 0L,
            ),
        )

        assertFalse(decision.shouldRemind)
        assertEquals(ReminderSuppressionReason.RECENTLY_REVIEWED, decision.suppressionReason)
        assertEquals(10 * 60_000L, decision.nextEligibleAtMillis)
    }

    @Test
    fun remindsAfterReviewIntervalPasses() {
        val decision = ReminderRules.evaluate(
            ReminderRuleInput(
                pendingTaskCount = 1,
                nowMillis = 10 * 60_000L,
                localMinuteOfDay = 9 * 60,
                reminderIntervalMinutes = 10,
                lastReviewedAtMillis = 0L,
            ),
        )

        assertTrue(decision.shouldRemind)
    }

    @Test
    fun quietHoursTakePriorityOverSnoozeAndRecentReview() {
        val decision = ReminderRules.evaluate(
            ReminderRuleInput(
                pendingTaskCount = 1,
                nowMillis = 1_000L,
                localMinuteOfDay = 23 * 60,
                reminderIntervalMinutes = 10,
                lastReviewedAtMillis = 900L,
                snoozedUntilMillis = 2_000L,
                quietHours = QuietHours(isEnabled = true),
            ),
        )

        assertFalse(decision.shouldRemind)
        assertEquals(ReminderSuppressionReason.QUIET_HOURS, decision.suppressionReason)
    }

    @Test
    fun suppressesScreenActivityModeWhenUsageAccessIsMissing() {
        val decision = ReminderRules.evaluate(
            ReminderRuleInput(
                pendingTaskCount = 1,
                nowMillis = 1_000L,
                localMinuteOfDay = 9 * 60,
                reminderIntervalMinutes = 10,
                screenActivityRequirement = ScreenActivityRequirement(
                    isEnabled = true,
                    hasUsageAccess = false,
                    hasRecentActivity = false,
                ),
            ),
        )

        assertFalse(decision.shouldRemind)
        assertEquals(ReminderSuppressionReason.USAGE_ACCESS_REQUIRED, decision.suppressionReason)
    }

    @Test
    fun suppressesScreenActivityModeWhenRecentActivityIsMissing() {
        val decision = ReminderRules.evaluate(
            ReminderRuleInput(
                pendingTaskCount = 1,
                nowMillis = 1_000L,
                localMinuteOfDay = 9 * 60,
                reminderIntervalMinutes = 10,
                screenActivityRequirement = ScreenActivityRequirement(
                    isEnabled = true,
                    hasUsageAccess = true,
                    hasRecentActivity = false,
                ),
            ),
        )

        assertFalse(decision.shouldRemind)
        assertEquals(
            ReminderSuppressionReason.NO_RECENT_SCREEN_ACTIVITY,
            decision.suppressionReason,
        )
    }

    @Test
    fun remindsWhenScreenActivityModeHasRecentActivity() {
        val decision = ReminderRules.evaluate(
            ReminderRuleInput(
                pendingTaskCount = 1,
                nowMillis = 1_000L,
                localMinuteOfDay = 9 * 60,
                reminderIntervalMinutes = 10,
                screenActivityRequirement = ScreenActivityRequirement(
                    isEnabled = true,
                    hasUsageAccess = true,
                    hasRecentActivity = true,
                ),
            ),
        )

        assertTrue(decision.shouldRemind)
    }
}
