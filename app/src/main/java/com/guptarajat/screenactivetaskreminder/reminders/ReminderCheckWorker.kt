package com.guptarajat.screenactivetaskreminder.reminders

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderCheckWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result =
        runCatching {
            ReminderNotificationCoordinator.evaluateAndNotify(applicationContext)
            ReminderScheduler.scheduleFollowUp(applicationContext)
        }.fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() },
        )
}
