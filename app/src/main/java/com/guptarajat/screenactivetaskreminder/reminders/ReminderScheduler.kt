package com.guptarajat.screenactivetaskreminder.reminders

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.guptarajat.screenactivetaskreminder.settings.SettingsStore
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

private const val REMINDER_WORK_NAME = "task_reminder_check"
private const val REMINDER_WORK_TAG = "task_reminder"

object ReminderScheduler {
    suspend fun scheduleNext(
        context: Context,
        nowMillis: Long = System.currentTimeMillis(),
    ) {
        enqueue(
            context = context,
            existingWorkPolicy = ExistingWorkPolicy.REPLACE,
            nowMillis = nowMillis,
        )
    }

    suspend fun scheduleFollowUp(
        context: Context,
        nowMillis: Long = System.currentTimeMillis(),
    ) {
        enqueue(
            context = context,
            existingWorkPolicy = ExistingWorkPolicy.APPEND_OR_REPLACE,
            nowMillis = nowMillis,
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context.applicationContext)
            .cancelUniqueWork(REMINDER_WORK_NAME)
    }

    private suspend fun enqueue(
        context: Context,
        existingWorkPolicy: ExistingWorkPolicy,
        nowMillis: Long,
    ) {
        val appContext = context.applicationContext
        val settings = SettingsStore(appContext).settings.first()
        val delayMillis = nextReminderDelayMillis(
            settings = settings,
            nowMillis = nowMillis,
            localMinuteOfDay = localMinuteOfDay(nowMillis),
        )
        val reminderWork = OneTimeWorkRequestBuilder<ReminderCheckWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .addTag(REMINDER_WORK_TAG)
            .build()

        WorkManager.getInstance(appContext)
            .enqueueUniqueWork(REMINDER_WORK_NAME, existingWorkPolicy, reminderWork)
    }
}
