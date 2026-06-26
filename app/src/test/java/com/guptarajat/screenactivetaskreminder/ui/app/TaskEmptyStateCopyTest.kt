package com.guptarajat.screenactivetaskreminder.ui.app

import com.guptarajat.screenactivetaskreminder.auth.AuthSession
import com.guptarajat.screenactivetaskreminder.data.local.CachedTask
import com.guptarajat.screenactivetaskreminder.data.local.TASK_STATUS_NEEDS_ACTION
import com.guptarajat.screenactivetaskreminder.data.repository.CachedTaskList
import com.guptarajat.screenactivetaskreminder.data.repository.TaskCacheSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TaskEmptyStateCopyTest {
    @Test
    fun todayGuidanceSendsSignedOutUsersToSettings() {
        val copy = todayEmptyStateCopy(
            taskCacheSnapshot = TaskCacheSnapshot(),
            authSession = AuthSession(),
        )

        assertEquals("Start with Google Tasks", copy?.title)
        assertEquals(EmptyStateAction.OPEN_SETTINGS, copy?.action)
        assertEquals(EmptyStateTone.GUIDANCE, copy?.tone)
    }

    @Test
    fun todayGuidanceSendsSignedInUsersToTasksForFirstSync() {
        val copy = todayEmptyStateCopy(
            taskCacheSnapshot = TaskCacheSnapshot(),
            authSession = signedInSession(),
        )

        assertEquals("Bring in your tasks", copy?.title)
        assertEquals(EmptyStateAction.OPEN_TASKS, copy?.action)
    }

    @Test
    fun todayGuidanceShowsRecoverableSyncErrorBeforeEmptySuccess() {
        val copy = todayEmptyStateCopy(
            taskCacheSnapshot = syncedSnapshot(lastError = "Network unavailable."),
            authSession = signedInSession(),
        )

        assertEquals("Using saved tasks", copy?.title)
        assertEquals("Network unavailable.", copy?.detail)
        assertEquals(EmptyStateTone.ERROR, copy?.tone)
    }

    @Test
    fun todayGuidanceIsHiddenWhenPendingTasksExist() {
        val copy = todayEmptyStateCopy(
            taskCacheSnapshot = syncedSnapshot(pendingTasks = listOf(sampleTask())),
            authSession = signedInSession(),
        )

        assertNull(copy)
    }

    @Test
    fun tasksEmptyStateMakesSyncFailureRecoverable() {
        val copy = tasksEmptyStateCopy(
            taskCacheSnapshot = syncedSnapshot(lastError = "Timeout."),
            authSession = signedInSession(),
        )

        assertEquals("No tasks shown after sync issue", copy.title)
        assertEquals("Timeout.", copy.detail)
        assertEquals(EmptyStateAction.SYNC_TASKS, copy.action)
        assertEquals(EmptyStateTone.ERROR, copy.tone)
    }

    @Test
    fun tasksEmptyStateExplainsSuccessfulEmptyCache() {
        val copy = tasksEmptyStateCopy(
            taskCacheSnapshot = syncedSnapshot(),
            authSession = signedInSession(),
        )

        assertEquals("No pending tasks", copy.title)
        assertEquals(EmptyStateAction.SYNC_TASKS, copy.action)
        assertEquals(EmptyStateIcon.CHECK_CIRCLE, copy.icon)
    }

    @Test
    fun oauthSetupFailureHasConcreteRecoveryCopy() {
        val copy = authStatusRecoveryCopy(
            "Google sign-in needs OAuth setup before real accounts can connect.",
        )

        assertEquals(
            "Finish the Google Cloud OAuth setup, rebuild the app, then try signing in again.",
            copy,
        )
    }

    @Test
    fun noGoogleAccountFailureOpensAndroidAccountSetupPath() {
        val copy = authStatusRecoveryCopy(
            "No Google account is available on this Android device.",
        )

        assertEquals(
            "Add a Google account in Android settings, then return here and tap Sign in with Google again.",
            copy,
        )
    }

    @Test
    fun missingGoogleAccountSupportPointsToCompatibleTestDevice() {
        val copy = authStatusRecoveryCopy(
            "This Android device does not include Google account support.",
        )

        assertEquals(
            "Use a Google Play emulator image or a real Android phone, then try signing in again.",
            copy,
        )
    }

    private fun signedInSession(): AuthSession =
        AuthSession(accountId = "account-1", email = "tester@example.com")

    private fun syncedSnapshot(
        pendingTasks: List<CachedTask> = emptyList(),
        lastError: String? = null,
    ): TaskCacheSnapshot =
        TaskCacheSnapshot(
            pendingTasks = pendingTasks,
            taskLists = listOf(
                CachedTaskList(
                    id = "list-1",
                    title = "Inbox",
                    updatedAtMillis = null,
                    isSelected = true,
                ),
            ),
            selectedTaskListCount = 1,
            lastSuccessfulSyncAtMillis = 100L,
            lastError = lastError,
        )

    private fun sampleTask(): CachedTask =
        CachedTask(
            id = "task-1",
            taskListId = "list-1",
            taskListTitle = "Inbox",
            title = "Pay bill",
            notes = null,
            status = TASK_STATUS_NEEDS_ACTION,
            dueAtMillis = null,
            completedAtMillis = null,
            updatedAtMillis = null,
            position = null,
        )
}
