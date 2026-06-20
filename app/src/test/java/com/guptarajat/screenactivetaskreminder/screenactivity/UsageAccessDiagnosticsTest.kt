package com.guptarajat.screenactivetaskreminder.screenactivity

import android.app.usage.UsageEvents
import org.junit.Assert.assertEquals
import org.junit.Test

class UsageAccessDiagnosticsTest {
    @Test
    fun summarizesTargetScreenActivityEventTypes() {
        val summary = summarizeScreenActivityEventTypes(
            listOf(
                UsageEvents.Event.SCREEN_INTERACTIVE,
                UsageEvents.Event.SCREEN_INTERACTIVE,
                UsageEvents.Event.SCREEN_NON_INTERACTIVE,
                UsageEvents.Event.ACTIVITY_RESUMED,
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_PAUSED,
                999,
            ),
        )

        assertEquals(2, summary.countFor(ScreenActivityUsageEventType.SCREEN_INTERACTIVE))
        assertEquals(1, summary.countFor(ScreenActivityUsageEventType.SCREEN_NON_INTERACTIVE))
        assertEquals(1, summary.countFor(ScreenActivityUsageEventType.ACTIVITY_RESUMED))
        assertEquals(3, summary.countFor(ScreenActivityUsageEventType.ACTIVITY_PAUSED))
    }

    @Test
    fun includesZeroCountsForMissingTargetEvents() {
        val summary = summarizeScreenActivityEventTypes(
            listOf(UsageEvents.Event.SCREEN_INTERACTIVE),
        )

        assertEquals(1, summary.countFor(ScreenActivityUsageEventType.SCREEN_INTERACTIVE))
        assertEquals(0, summary.countFor(ScreenActivityUsageEventType.SCREEN_NON_INTERACTIVE))
        assertEquals(0, summary.countFor(ScreenActivityUsageEventType.ACTIVITY_RESUMED))
        assertEquals(0, summary.countFor(ScreenActivityUsageEventType.ACTIVITY_PAUSED))
    }

    private fun List<UsageAccessEventCount>.countFor(
        type: ScreenActivityUsageEventType,
    ): Int = first { it.type == type }.count
}
