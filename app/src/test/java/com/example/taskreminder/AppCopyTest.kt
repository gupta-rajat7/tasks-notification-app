package com.example.taskreminder

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppCopyTest {
    @Test
    fun appNameMatchesLaunchScreenCopy() {
        assertEquals("Screen Active Task Reminder", appName())
    }

    @Test
    fun placeholderNavigationPointsToNextAppShellDestinations() {
        val navigationDirection = placeholderNavigationDirection()

        assertTrue(navigationDirection.contains("Today"))
        assertTrue(navigationDirection.contains("Tasks"))
        assertTrue(navigationDirection.contains("Settings"))
    }
}
