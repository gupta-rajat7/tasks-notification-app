package com.guptarajat.screenactivetaskreminder.reminders

import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderNotificationCoordinatorTest {
    @Test
    fun notificationTitleUsesSingularTaskCopy() {
        assertEquals("1 pending task", reminderNotificationTitle(1))
    }

    @Test
    fun notificationTitleUsesPluralTaskCopy() {
        assertEquals("3 pending tasks", reminderNotificationTitle(3))
    }

    @Test
    fun snoozeUntilAddsConfiguredMinutes() {
        assertEquals(1_810_000L, snoozeUntilMillis(nowMillis = 10_000L, snoozeMinutes = 30))
    }

    @Test
    fun statusMessageExplainsMissingNotificationPermission() {
        val message = reminderNotificationStatusMessage(
            ReminderNotificationCheckResult(
                pendingTaskCount = 2,
                decision = ReminderDecision.remind(),
                didPostNotification = false,
                missingNotificationPermission = true,
            ),
        )

        assertEquals("Notifications are off. Enable notifications to receive reminders.", message)
    }

    @Test
    fun statusMessageExplainsNoPendingTasksSuppression() {
        val message = reminderNotificationStatusMessage(
            ReminderNotificationCheckResult(
                pendingTaskCount = 0,
                decision = ReminderDecision.suppress(ReminderSuppressionReason.NO_PENDING_TASKS),
                didPostNotification = false,
            ),
        )

        assertEquals("No reminder sent because there are no pending cached tasks.", message)
    }
}
