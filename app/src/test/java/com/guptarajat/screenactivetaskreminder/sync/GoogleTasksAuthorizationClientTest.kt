package com.guptarajat.screenactivetaskreminder.sync

import org.junit.Assert.assertEquals
import org.junit.Test

class GoogleTasksAuthorizationClientTest {
    @Test
    fun requestsReadOnlyGoogleTasksScope() {
        assertEquals(
            "https://www.googleapis.com/auth/tasks.readonly",
            GOOGLE_TASKS_READONLY_SCOPE,
        )
    }
}
