package com.guptarajat.screenactivetaskreminder

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppCopyTest {
    @Test
    fun appNameMatchesLaunchScreenCopy() {
        assertEquals("Screen Active Task Reminder", appName())
    }

    @Test
    fun appSectionsIncludeBottomNavigationDestinations() {
        val navigationLabels = appSections().map { it.label }

        assertTrue(navigationLabels.contains("Today"))
        assertTrue(navigationLabels.contains("Tasks"))
        assertTrue(navigationLabels.contains("Settings"))
    }

    @Test
    fun unknownRouteFallsBackToTodaySection() {
        val section = appSectionForRoute("missing")

        assertEquals(TODAY_ROUTE, section.route)
    }
}
