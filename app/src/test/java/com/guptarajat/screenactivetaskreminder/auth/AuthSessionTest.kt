package com.guptarajat.screenactivetaskreminder.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthSessionTest {
    @Test
    fun blankAccountIsSignedOut() {
        assertFalse(AuthSession().isSignedIn)
        assertFalse(AuthSession(accountId = "").isSignedIn)
    }

    @Test
    fun accountIdMarksSessionSignedIn() {
        assertTrue(AuthSession(accountId = "person@example.com").isSignedIn)
    }

    @Test
    fun displayLabelPrefersDisplayNameThenEmail() {
        assertEquals(
            "Tanu",
            AuthSession(
                accountId = "person@example.com",
                email = "person@example.com",
                displayName = "Tanu",
            ).displayLabel,
        )
        assertEquals(
            "person@example.com",
            AuthSession(
                accountId = "person@example.com",
                email = "person@example.com",
            ).displayLabel,
        )
        assertEquals("Google account", AuthSession(accountId = "account-id").displayLabel)
    }
}
