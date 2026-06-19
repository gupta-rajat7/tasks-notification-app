package com.guptarajat.screenactivetaskreminder.auth

import android.content.Context
import android.util.Base64
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import java.security.SecureRandom

class GoogleSignInClient(
    context: Context,
    private val config: GoogleSignInConfig,
) {
    private val credentialManager = CredentialManager.create(context.applicationContext)

    suspend fun signIn(activityContext: Context): GoogleSignInResult {
        if (!config.isConfigured) {
            return GoogleSignInResult.MissingConfiguration
        }

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetSignInWithGoogleOption.Builder(
                    serverClientId = config.webClientId,
                )
                    .setNonce(generateSecureRandomNonce())
                    .build(),
            )
            .build()

        return try {
            credentialManager.getCredential(
                request = request,
                context = activityContext,
            ).toGoogleSignInResult()
        } catch (error: GetCredentialCancellationException) {
            GoogleSignInResult.Cancelled
        } catch (error: NoCredentialException) {
            GoogleSignInResult.NoCredential
        } catch (error: GetCredentialException) {
            GoogleSignInResult.Failure(error.message ?: "Google sign-in could not start.")
        } catch (error: GoogleIdTokenParsingException) {
            GoogleSignInResult.Failure("Google returned an invalid sign-in response.")
        } catch (error: IllegalArgumentException) {
            GoogleSignInResult.Failure("Google returned an unsupported sign-in response.")
        }
    }

    suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    private fun GetCredentialResponse.toGoogleSignInResult(): GoogleSignInResult {
        val credential = credential
        if (
            credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return GoogleSignInResult.Failure("Google returned an unsupported credential.")
        }

        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
        return GoogleSignInResult.Success(
            AuthSession(
                accountId = googleCredential.id,
                email = googleCredential.id,
                displayName = googleCredential.displayName,
                profilePictureUri = googleCredential.profilePictureUri?.toString(),
            ),
        )
    }
}

sealed interface GoogleSignInResult {
    data class Success(val session: AuthSession) : GoogleSignInResult
    data object MissingConfiguration : GoogleSignInResult
    data object Cancelled : GoogleSignInResult
    data object NoCredential : GoogleSignInResult
    data class Failure(val message: String) : GoogleSignInResult
}

fun GoogleSignInResult.userMessage(): String? = when (this) {
    is GoogleSignInResult.Success -> null
    GoogleSignInResult.MissingConfiguration ->
        "Google sign-in needs OAuth setup before real accounts can connect."
    GoogleSignInResult.Cancelled -> "Google sign-in was cancelled."
    GoogleSignInResult.NoCredential -> "No Google account was available on this device."
    is GoogleSignInResult.Failure -> message
}

private fun generateSecureRandomNonce(byteLength: Int = 32): String {
    val randomBytes = ByteArray(byteLength)
    SecureRandom().nextBytes(randomBytes)
    return Base64.encodeToString(
        randomBytes,
        Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING,
    )
}
