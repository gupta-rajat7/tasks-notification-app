package com.guptarajat.screenactivetaskreminder.auth

data class AuthSession(
    val accountId: String? = null,
    val email: String? = null,
    val displayName: String? = null,
    val profilePictureUri: String? = null,
) {
    val isSignedIn: Boolean
        get() = !accountId.isNullOrBlank()

    val displayLabel: String
        get() = displayName?.takeIf { it.isNotBlank() }
            ?: email?.takeIf { it.isNotBlank() }
            ?: "Google account"
}
