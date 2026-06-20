package com.guptarajat.screenactivetaskreminder.screenactivity

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings

private const val DEFAULT_USAGE_QUERY_WINDOW_MILLIS = 60 * 60 * 1_000L
private const val DEFAULT_SCREEN_ACTIVITY_REMINDER_WINDOW_MILLIS = 10 * 60 * 1_000L

enum class ScreenActivityUsageEventType(
    val eventType: Int,
    val label: String,
) {
    SCREEN_INTERACTIVE(UsageEvents.Event.SCREEN_INTERACTIVE, "Screen interactive"),
    SCREEN_NON_INTERACTIVE(UsageEvents.Event.SCREEN_NON_INTERACTIVE, "Screen non-interactive"),
    ACTIVITY_RESUMED(UsageEvents.Event.ACTIVITY_RESUMED, "Activity resumed"),
    ACTIVITY_PAUSED(UsageEvents.Event.ACTIVITY_PAUSED, "Activity paused"),
}

data class UsageAccessEventCount(
    val type: ScreenActivityUsageEventType,
    val count: Int,
)

data class UsageAccessDiagnosticSnapshot(
    val hasUsageAccess: Boolean,
    val canOpenUsageAccessSettings: Boolean,
    val queryWindowMinutes: Int = 60,
    val scannedAtMillis: Long? = null,
    val totalEventCount: Int = 0,
    val targetEventCounts: List<UsageAccessEventCount> = emptyScreenActivityEventCounts(),
    val issue: String? = null,
)

data class ScreenActivityReminderSnapshot(
    val isEnabled: Boolean,
    val hasUsageAccess: Boolean,
    val hasRecentActivity: Boolean,
    val queryWindowMinutes: Int = 10,
    val scannedAtMillis: Long? = null,
    val totalEventCount: Int = 0,
    val issue: String? = null,
)

object UsageAccessDiagnostics {
    fun accessSnapshot(context: Context): UsageAccessDiagnosticSnapshot =
        UsageAccessDiagnosticSnapshot(
            hasUsageAccess = hasUsageAccess(context),
            canOpenUsageAccessSettings = canOpenUsageAccessSettings(context),
        )

    fun openUsageAccessSettings(context: Context): Boolean {
        val appContext = context.applicationContext
        val intent = usageAccessSettingsIntent()
        if (intent.resolveActivity(appContext.packageManager) == null) {
            return false
        }
        return runCatching {
            appContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }.isSuccess
    }

    fun scanRecentActivity(
        context: Context,
        nowMillis: Long = System.currentTimeMillis(),
        queryWindowMillis: Long = DEFAULT_USAGE_QUERY_WINDOW_MILLIS,
    ): UsageAccessDiagnosticSnapshot {
        val appContext = context.applicationContext
        val hasAccess = hasUsageAccess(appContext)
        if (!hasAccess) {
            return UsageAccessDiagnosticSnapshot(
                hasUsageAccess = false,
                canOpenUsageAccessSettings = canOpenUsageAccessSettings(appContext),
                queryWindowMinutes = queryWindowMillis.toWindowMinutes(),
                issue = "Usage Access is off.",
            )
        }

        return runCatching {
            val eventTypes = queryUsageEventTypes(
                context = appContext,
                startMillis = nowMillis - queryWindowMillis,
                endMillis = nowMillis,
            )

            UsageAccessDiagnosticSnapshot(
                hasUsageAccess = true,
                canOpenUsageAccessSettings = canOpenUsageAccessSettings(appContext),
                queryWindowMinutes = queryWindowMillis.toWindowMinutes(),
                scannedAtMillis = nowMillis,
                totalEventCount = eventTypes.size,
                targetEventCounts = summarizeScreenActivityEventTypes(eventTypes),
            )
        }.getOrElse { error ->
            UsageAccessDiagnosticSnapshot(
                hasUsageAccess = true,
                canOpenUsageAccessSettings = canOpenUsageAccessSettings(appContext),
                queryWindowMinutes = queryWindowMillis.toWindowMinutes(),
                scannedAtMillis = nowMillis,
                issue = error.localizedMessage ?: "Usage events could not be read.",
            )
        }
    }
}

object ScreenActivityReminderGate {
    fun snapshot(
        context: Context,
        isEnabled: Boolean,
        nowMillis: Long = System.currentTimeMillis(),
        queryWindowMillis: Long = DEFAULT_SCREEN_ACTIVITY_REMINDER_WINDOW_MILLIS,
    ): ScreenActivityReminderSnapshot {
        val appContext = context.applicationContext
        if (!isEnabled) {
            return ScreenActivityReminderSnapshot(
                isEnabled = false,
                hasUsageAccess = hasUsageAccess(appContext),
                hasRecentActivity = true,
                queryWindowMinutes = queryWindowMillis.toWindowMinutes(),
            )
        }

        val hasAccess = hasUsageAccess(appContext)
        if (!hasAccess) {
            return ScreenActivityReminderSnapshot(
                isEnabled = true,
                hasUsageAccess = false,
                hasRecentActivity = false,
                queryWindowMinutes = queryWindowMillis.toWindowMinutes(),
                issue = "Usage Access is off.",
            )
        }

        return runCatching {
            val eventTypes = queryUsageEventTypes(
                context = appContext,
                startMillis = nowMillis - queryWindowMillis,
                endMillis = nowMillis,
            )
            screenActivityReminderSnapshotFromEventTypes(
                eventTypes = eventTypes,
                isEnabled = true,
                hasUsageAccess = true,
                nowMillis = nowMillis,
                queryWindowMillis = queryWindowMillis,
            )
        }.getOrElse { error ->
            ScreenActivityReminderSnapshot(
                isEnabled = true,
                hasUsageAccess = true,
                hasRecentActivity = false,
                queryWindowMinutes = queryWindowMillis.toWindowMinutes(),
                scannedAtMillis = nowMillis,
                issue = error.localizedMessage ?: "Recent screen activity could not be read.",
            )
        }
    }
}

fun summarizeScreenActivityEventTypes(eventTypes: Iterable<Int>): List<UsageAccessEventCount> {
    val countsByType = eventTypes.groupingBy { it }.eachCount()
    return ScreenActivityUsageEventType.entries.map { targetType ->
        UsageAccessEventCount(
            type = targetType,
            count = countsByType[targetType.eventType] ?: 0,
        )
    }
}

internal fun screenActivityReminderSnapshotFromEventTypes(
    eventTypes: Iterable<Int>,
    isEnabled: Boolean,
    hasUsageAccess: Boolean,
    nowMillis: Long,
    queryWindowMillis: Long = DEFAULT_SCREEN_ACTIVITY_REMINDER_WINDOW_MILLIS,
): ScreenActivityReminderSnapshot =
    ScreenActivityReminderSnapshot(
        isEnabled = isEnabled,
        hasUsageAccess = hasUsageAccess,
        hasRecentActivity = !isEnabled ||
            (hasUsageAccess && hasRecentScreenActivityEvidence(eventTypes)),
        queryWindowMinutes = queryWindowMillis.toWindowMinutes(),
        scannedAtMillis = nowMillis,
        totalEventCount = eventTypes.count(),
    )

internal fun hasRecentScreenActivityEvidence(eventTypes: Iterable<Int>): Boolean {
    val recentActivityEventTypes = setOf(
        ScreenActivityUsageEventType.SCREEN_INTERACTIVE.eventType,
        ScreenActivityUsageEventType.ACTIVITY_RESUMED.eventType,
        ScreenActivityUsageEventType.ACTIVITY_PAUSED.eventType,
    )
    return eventTypes.any { it in recentActivityEventTypes }
}

private fun queryUsageEventTypes(
    context: Context,
    startMillis: Long,
    endMillis: Long,
): List<Int> {
    val usageStatsManager = context.getSystemService(UsageStatsManager::class.java)
    val usageEvents = usageStatsManager.queryEvents(startMillis, endMillis)
    val usageEvent = UsageEvents.Event()
    val eventTypes = mutableListOf<Int>()
    while (usageEvents != null && usageEvents.hasNextEvent()) {
        usageEvents.getNextEvent(usageEvent)
        eventTypes.add(usageEvent.eventType)
    }
    return eventTypes
}

private fun emptyScreenActivityEventCounts(): List<UsageAccessEventCount> =
    ScreenActivityUsageEventType.entries.map { targetType ->
        UsageAccessEventCount(type = targetType, count = 0)
    }

private fun usageAccessSettingsIntent(): Intent =
    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)

@Suppress("DEPRECATION")
private fun hasUsageAccess(context: Context): Boolean {
    val appOpsManager = context.getSystemService(AppOpsManager::class.java)
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOpsManager.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName,
        )
    } else {
        appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName,
        )
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

private fun canOpenUsageAccessSettings(context: Context): Boolean =
    usageAccessSettingsIntent().resolveActivity(context.packageManager) != null

private fun Long.toWindowMinutes(): Int =
    (this / 60_000L).coerceAtLeast(1L).toInt()
