package com.guptarajat.screenactivetaskreminder.auth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleSignInConfigTest {
    @Test
    fun blankWebClientIdIsNotConfigured() {
        assertFalse(GoogleSignInConfig(webClientId = "").isConfigured)
    }

    @Test
    fun placeholderWebClientIdIsNotConfigured() {
        assertFalse(
            GoogleSignInConfig(
                webClientId = "REPLACE_WITH_GOOGLE_WEB_CLIENT_ID",
            ).isConfigured,
        )
    }

    @Test
    fun realWebClientIdIsConfigured() {
        assertTrue(
            GoogleSignInConfig(
                webClientId = "1234567890-example.apps.googleusercontent.com",
            ).isConfigured,
        )
    }

    @Test
    fun malformedWebClientIdIsNotConfigured() {
        assertFalse(
            GoogleSignInConfig(
                webClientId = "not-a-google-client-id",
            ).isConfigured,
        )
    }
}
