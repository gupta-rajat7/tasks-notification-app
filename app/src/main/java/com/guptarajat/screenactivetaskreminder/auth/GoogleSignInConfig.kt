package com.guptarajat.screenactivetaskreminder.auth

private const val WEB_CLIENT_ID_PLACEHOLDER = "REPLACE_WITH_GOOGLE_WEB_CLIENT_ID"

data class GoogleSignInConfig(
    val webClientId: String,
) {
    val isConfigured: Boolean
        get() = webClientId.isNotBlank() && webClientId != WEB_CLIENT_ID_PLACEHOLDER
}
