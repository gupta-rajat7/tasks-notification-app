package com.guptarajat.screenactivetaskreminder.sync

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine

internal const val GOOGLE_TASKS_READONLY_SCOPE =
    "https://www.googleapis.com/auth/tasks.readonly"

internal class GoogleTasksAuthorizationClient(context: Context) {
    private val appContext = context.applicationContext

    suspend fun requestAccess(activity: Activity): GoogleTasksAuthorizationResult =
        try {
            Identity.getAuthorizationClient(activity)
                .authorize(buildAuthorizationRequest())
                .awaitResult()
                .toGoogleTasksAuthorizationResult()
        } catch (error: CancellationException) {
            GoogleTasksAuthorizationResult.Cancelled
        } catch (error: ApiException) {
            GoogleTasksAuthorizationResult.Failure(
                error.localizedMessage ?: "Google Tasks permission could not be requested.",
            )
        } catch (error: RuntimeException) {
            GoogleTasksAuthorizationResult.Failure(
                error.localizedMessage ?: "Google Tasks permission could not be requested.",
            )
        }

    fun readAuthorizationResult(intent: Intent?): GoogleTasksAuthorizationResult {
        if (intent == null) {
            return GoogleTasksAuthorizationResult.Cancelled
        }

        return try {
            Identity.getAuthorizationClient(appContext)
                .getAuthorizationResultFromIntent(intent)
                .toGoogleTasksAuthorizationResult()
        } catch (error: ApiException) {
            GoogleTasksAuthorizationResult.Failure(
                error.localizedMessage ?: "Google Tasks permission was not granted.",
            )
        } catch (error: RuntimeException) {
            GoogleTasksAuthorizationResult.Failure(
                error.localizedMessage ?: "Google Tasks permission was not granted.",
            )
        }
    }

    private fun buildAuthorizationRequest(): AuthorizationRequest =
        AuthorizationRequest.builder()
            .setRequestedScopes(listOf(Scope(GOOGLE_TASKS_READONLY_SCOPE)))
            .build()
}

internal sealed interface GoogleTasksAuthorizationResult {
    data class Authorized(val accessToken: String) : GoogleTasksAuthorizationResult
    data class NeedsUserConsent(val pendingIntent: PendingIntent) : GoogleTasksAuthorizationResult
    data object Cancelled : GoogleTasksAuthorizationResult
    data class Failure(val message: String) : GoogleTasksAuthorizationResult
}

internal fun GoogleTasksAuthorizationResult.userMessage(): String? = when (this) {
    is GoogleTasksAuthorizationResult.Authorized -> null
    is GoogleTasksAuthorizationResult.NeedsUserConsent -> null
    GoogleTasksAuthorizationResult.Cancelled -> "Google Tasks permission was cancelled."
    is GoogleTasksAuthorizationResult.Failure -> message
}

private fun AuthorizationResult.toGoogleTasksAuthorizationResult(): GoogleTasksAuthorizationResult {
    if (hasResolution()) {
        val pendingIntent = pendingIntent
            ?: return GoogleTasksAuthorizationResult.Failure(
                "Google Tasks permission needs approval, but Android did not return a permission screen.",
            )
        return GoogleTasksAuthorizationResult.NeedsUserConsent(pendingIntent)
    }

    val token = accessToken
    return if (token.isNullOrBlank()) {
        GoogleTasksAuthorizationResult.Failure("Google did not return a Tasks access token.")
    } else {
        GoogleTasksAuthorizationResult.Authorized(token)
    }
}

private suspend fun <T> Task<T>.awaitResult(): T =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            if (continuation.isActive) {
                continuation.resume(result)
            }
        }
        addOnFailureListener { error ->
            if (continuation.isActive) {
                continuation.resumeWithException(error)
            }
        }
        addOnCanceledListener {
            if (continuation.isActive) {
                continuation.cancel(CancellationException("Google authorization was cancelled."))
            }
        }
    }
