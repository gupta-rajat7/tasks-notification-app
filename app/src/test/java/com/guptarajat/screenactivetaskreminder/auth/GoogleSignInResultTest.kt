package com.guptarajat.screenactivetaskreminder.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GoogleSignInResultTest {
    @Test
    fun successHasNoUserMessage() {
        val result = GoogleSignInResult.Success(
            AuthSession(accountId = "person@example.com"),
        )

        assertNull(result.userMessage())
    }

    @Test
    fun noCredentialExplainsMissingAndroidGoogleAccount() {
        assertEquals(
            "No Google account is available on this Android device.",
            GoogleSignInResult.NoCredential.userMessage(),
        )
    }
}
