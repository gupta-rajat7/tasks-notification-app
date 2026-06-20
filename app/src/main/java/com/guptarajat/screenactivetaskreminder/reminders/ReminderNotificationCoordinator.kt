package com.guptarajat.screenactivetaskreminder.reminders

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.guptarajat.screenactivetaskreminder.MainActivity
import com.guptarajat.screenactivetaskreminder.R
import com.guptarajat.screenactivetaskreminder.data.local.TASK_STATUS_COMPLETED
import com.guptarajat.screenactivetaskreminder.data.local.TaskReminderDatabase
import com.guptarajat.screenactivetaskreminder.settings.SettingsStore
import kotlinx.coroutines.flow.first

private const val MILLIS_PER_MINUTE = 60_000L

const val REMINDER_NOTIFICATION_CHANNEL_ID = "task_review_reminders"
const val REMINDER_NOTIFICATION_ID = 1001
const val ACTION_SNOOZE_REMINDER = "com.guptarajat.screenactivetaskreminder.action.SNOOZE_REMINDER"
const val ACTION_DONE_FOR_NOW = "com.guptarajat.screenactivetaskreminder.action.DONE_FOR_NOW"
const val ACTION_REVIEW_TASKS = "com.guptarajat.screenactivetaskreminder.action.REVIEW_TASKS"

data class ReminderNotificationCheckResult(
    val pendingTaskCount: Int,
    val decision: ReminderDecision,
    val didPostNotification: Boolean,
    val missingNotificationPermission: Boolean = false,
)

object ReminderNotificationCoordinator {
    fun ensureNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            REMINDER_NOTIFICATION_CHANNEL_ID,
            "Task review reminders",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Reminders to review pending Google Tasks."
        }

        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    fun areNotificationsEnabled(context: Context): Boolean {
        val appContext = context.applicationContext
        val hasRuntimePermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        return hasRuntimePermission && NotificationManagerCompat.from(appContext).areNotificationsEnabled()
    }

    suspend fun evaluateAndNotify(
        context: Context,
        nowMillis: Long = System.currentTimeMillis(),
    ): ReminderNotificationCheckResult {
        val appContext = context.applicationContext
        ensureNotificationChannel(appContext)

        val settingsStore = SettingsStore(appContext)
        val settings = settingsStore.settings.first()
        val pendingTaskCount = TaskReminderDatabase.getInstance(appContext)
            .taskDao()
            .countPendingTasksForSelectedLists(TASK_STATUS_COMPLETED)
        val decision = ReminderRules.evaluate(
            ReminderRuleInput(
                pendingTaskCount = pendingTaskCount,
                nowMillis = nowMillis,
                localMinuteOfDay = localMinuteOfDay(nowMillis),
                reminderIntervalMinutes = settings.reminderIntervalMinutes,
                lastReviewedAtMillis = settings.lastReviewedAtMillis,
                snoozedUntilMillis = settings.snoozedUntilMillis,
                quietHours = QuietHours(
                    isEnabled = settings.quietHoursEnabled,
                    startMinuteOfDay = settings.quietHoursStartMinuteOfDay,
                    endMinuteOfDay = settings.quietHoursEndMinuteOfDay,
                ),
            ),
        )

        if (!decision.shouldRemind) {
            return ReminderNotificationCheckResult(
                pendingTaskCount = pendingTaskCount,
                decision = decision,
                didPostNotification = false,
            )
        }

        if (!areNotificationsEnabled(appContext)) {
            return ReminderNotificationCheckResult(
                pendingTaskCount = pendingTaskCount,
                decision = decision,
                didPostNotification = false,
                missingNotificationPermission = true,
            )
        }

        val didPostNotification = postNotification(
            context = appContext,
            pendingTaskCount = pendingTaskCount,
        )
        return ReminderNotificationCheckResult(
            pendingTaskCount = pendingTaskCount,
            decision = decision,
            didPostNotification = didPostNotification,
            missingNotificationPermission = !didPostNotification,
        )
    }

    fun cancel(context: Context) {
        NotificationManagerCompat.from(context.applicationContext)
            .cancel(REMINDER_NOTIFICATION_ID)
    }

    @SuppressLint("MissingPermission")
    private fun postNotification(
        context: Context,
        pendingTaskCount: Int,
    ): Boolean {
        val notification = NotificationCompat.Builder(context, REMINDER_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(reminderNotificationTitle(pendingTaskCount))
            .setContentText(reminderNotificationBody(pendingTaskCount))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(reminderNotificationBody(pendingTaskCount)),
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(reviewPendingIntent(context))
            .addAction(
                0,
                "Review",
                reviewPendingIntent(context),
            )
            .addAction(
                0,
                "Snooze",
                broadcastPendingIntent(context, ACTION_SNOOZE_REMINDER, 2002),
            )
            .addAction(
                0,
                "Done for now",
                broadcastPendingIntent(context, ACTION_DONE_FOR_NOW, 2003),
            )
            .build()

        return runCatching {
            NotificationManagerCompat.from(context)
                .notify(REMINDER_NOTIFICATION_ID, notification)
        }.isSuccess
    }
}

fun reminderNotificationTitle(pendingTaskCount: Int): String =
    if (pendingTaskCount == 1) {
        "1 pending task"
    } else {
        "$pendingTaskCount pending tasks"
    }

fun reminderNotificationBody(pendingTaskCount: Int): String =
    if (pendingTaskCount == 1) {
        "Take a moment to review the task waiting in Google Tasks."
    } else {
        "Take a moment to review the tasks waiting in Google Tasks."
    }

fun reminderNotificationStatusMessage(result: ReminderNotificationCheckResult): String =
    when {
        result.didPostNotification -> "Reminder notification sent."
        result.missingNotificationPermission -> "Notifications are off. Enable notifications to receive reminders."
        result.decision.suppressionReason == ReminderSuppressionReason.NO_PENDING_TASKS ->
            "No reminder sent because there are no pending cached tasks."
        result.decision.suppressionReason == ReminderSuppressionReason.QUIET_HOURS ->
            "No reminder sent because quiet hours are active."
        result.decision.suppressionReason == ReminderSuppressionReason.SNOOZED ->
            "No reminder sent because reminders are snoozed."
        result.decision.suppressionReason == ReminderSuppressionReason.RECENTLY_REVIEWED ->
            "No reminder sent because tasks were reviewed recently."
        else -> "No reminder sent."
    }

fun snoozeUntilMillis(nowMillis: Long, snoozeMinutes: Int): Long =
    nowMillis + snoozeMinutes.coerceAtLeast(1) * MILLIS_PER_MINUTE

private fun reviewPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
        action = ACTION_REVIEW_TASKS
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    return PendingIntent.getActivity(
        context,
        2001,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}

private fun broadcastPendingIntent(
    context: Context,
    action: String,
    requestCode: Int,
): PendingIntent {
    val intent = Intent(context, ReminderNotificationActionReceiver::class.java).apply {
        this.action = action
    }
    return PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
