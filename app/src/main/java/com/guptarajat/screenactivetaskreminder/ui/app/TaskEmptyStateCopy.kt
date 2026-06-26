package com.guptarajat.screenactivetaskreminder.ui.app

import com.guptarajat.screenactivetaskreminder.auth.AuthSession
import com.guptarajat.screenactivetaskreminder.data.repository.TaskCacheSnapshot

internal enum class EmptyStateAction {
    OPEN_SETTINGS,
    OPEN_TASKS,
    SYNC_TASKS,
}

internal enum class EmptyStateIcon {
    CHECK_CIRCLE,
    ERROR,
    SETTINGS,
    SYNC,
}

internal enum class EmptyStateTone {
    GUIDANCE,
    ERROR,
}

internal data class EmptyStateCopy(
    val icon: EmptyStateIcon,
    val title: String,
    val body: String,
    val actionLabel: String,
    val action: EmptyStateAction,
    val detail: String? = null,
    val tone: EmptyStateTone = EmptyStateTone.GUIDANCE,
)

internal fun todayEmptyStateCopy(
    taskCacheSnapshot: TaskCacheSnapshot,
    authSession: AuthSession,
): EmptyStateCopy? = when {
    !authSession.isSignedIn -> EmptyStateCopy(
        icon = EmptyStateIcon.SETTINGS,
        title = "Start with Google Tasks",
        body = "Connect your Google account so reminders can watch your pending tasks.",
        actionLabel = "Go to Settings",
        action = EmptyStateAction.OPEN_SETTINGS,
    )

    !taskCacheSnapshot.lastError.isNullOrBlank() -> EmptyStateCopy(
        icon = EmptyStateIcon.ERROR,
        title = "Using saved tasks",
        body = "The last sync did not finish, so Today is showing the latest saved task cache.",
        detail = taskCacheSnapshot.lastError,
        actionLabel = "Open Tasks",
        action = EmptyStateAction.OPEN_TASKS,
        tone = EmptyStateTone.ERROR,
    )

    !taskCacheSnapshot.hasSyncedData -> EmptyStateCopy(
        icon = EmptyStateIcon.SYNC,
        title = "Bring in your tasks",
        body = "Sync once to fill the local task cache. After that, reminders can work from the saved task list.",
        actionLabel = "Open Tasks",
        action = EmptyStateAction.OPEN_TASKS,
    )

    taskCacheSnapshot.pendingTasks.isEmpty() -> EmptyStateCopy(
        icon = EmptyStateIcon.CHECK_CIRCLE,
        title = "No pending tasks",
        body = "You are clear for now. New pending Google Tasks will appear here after the next sync.",
        actionLabel = "View Tasks",
        action = EmptyStateAction.OPEN_TASKS,
    )

    else -> null
}

internal fun tasksEmptyStateCopy(
    taskCacheSnapshot: TaskCacheSnapshot,
    authSession: AuthSession,
): EmptyStateCopy = when {
    !authSession.isSignedIn -> EmptyStateCopy(
        icon = EmptyStateIcon.SETTINGS,
        title = "Sign in to load Google Tasks",
        body = "The app only reads your Google Tasks after you connect an account.",
        actionLabel = "Go to Settings",
        action = EmptyStateAction.OPEN_SETTINGS,
    )

    !taskCacheSnapshot.lastError.isNullOrBlank() -> EmptyStateCopy(
        icon = EmptyStateIcon.ERROR,
        title = "No tasks shown after sync issue",
        body = "The last sync did not finish. Try again when your connection is stable.",
        detail = taskCacheSnapshot.lastError,
        actionLabel = "Try again",
        action = EmptyStateAction.SYNC_TASKS,
        tone = EmptyStateTone.ERROR,
    )

    !taskCacheSnapshot.hasSyncedData -> EmptyStateCopy(
        icon = EmptyStateIcon.SYNC,
        title = "Ready for first sync",
        body = "Sync now to bring pending tasks into the on-device cache.",
        actionLabel = "Sync now",
        action = EmptyStateAction.SYNC_TASKS,
    )

    else -> EmptyStateCopy(
        icon = EmptyStateIcon.CHECK_CIRCLE,
        title = "No pending tasks",
        body = "Your synced Google Tasks do not have pending items right now.",
        actionLabel = "Sync again",
        action = EmptyStateAction.SYNC_TASKS,
    )
}

internal fun authStatusRecoveryCopy(authStatusMessage: String?): String? {
    if (authStatusMessage.isNullOrBlank()) {
        return null
    }

    return when {
        authStatusMessage.contains("OAuth setup", ignoreCase = true) ->
            "Finish the Google Cloud OAuth setup, rebuild the app, then try signing in again."

        authStatusMessage.contains("does not include Google account support", ignoreCase = true) ->
            "Use a Google Play emulator image or a real Android phone, then try signing in again."

        authStatusMessage.contains("cancelled", ignoreCase = true) ->
            "No changes were made. Tap Sign in with Google when you are ready."

        authStatusMessage.contains("No Google account", ignoreCase = true) ->
            "Add a Google account in Android settings, then return here and tap Sign in with Google again."

        else -> "Check the account setup and internet connection, then try signing in again."
    }
}
