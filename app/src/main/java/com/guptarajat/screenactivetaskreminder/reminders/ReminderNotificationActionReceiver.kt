package com.guptarajat.screenactivetaskreminder.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.guptarajat.screenactivetaskreminder.settings.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderNotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            runCatching {
                val settingsStore = SettingsStore(appContext)
                val nowMillis = System.currentTimeMillis()
                when (intent.action) {
                    ACTION_SNOOZE_REMINDER -> {
                        val settings = settingsStore.settings.first()
                        settingsStore.snoozeUntil(
                            snoozeUntilMillis(
                                nowMillis = nowMillis,
                                snoozeMinutes = settings.snoozeMinutes,
                            ),
                        )
                    }

                    ACTION_DONE_FOR_NOW -> {
                        settingsStore.recordReview(nowMillis)
                    }
                }
                ReminderNotificationCoordinator.cancel(appContext)
            }
            pendingResult.finish()
        }
    }
}
