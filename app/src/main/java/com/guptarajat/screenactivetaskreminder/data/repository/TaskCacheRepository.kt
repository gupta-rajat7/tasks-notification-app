package com.guptarajat.screenactivetaskreminder.data.repository

import androidx.room.withTransaction
import com.guptarajat.screenactivetaskreminder.data.local.TASK_STATUS_COMPLETED
import com.guptarajat.screenactivetaskreminder.data.local.SyncStateEntity
import com.guptarajat.screenactivetaskreminder.data.local.TaskReminderDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private const val DEFAULT_ACCOUNT_ID = "primary"

class TaskCacheRepository(
    private val database: TaskReminderDatabase,
) {
    val cacheSnapshot: Flow<TaskCacheSnapshot> = combine(
        database.taskDao().observePendingTasksForSelectedLists(TASK_STATUS_COMPLETED),
        database.taskListDao().observeSelectedTaskListCount(),
        database.syncStateDao().observeLatestSyncState(),
    ) { pendingTasks, selectedTaskListCount, syncState ->
        TaskCacheSnapshot(
            pendingTasks = pendingTasks,
            selectedTaskListCount = selectedTaskListCount,
            lastSuccessfulSyncAtMillis = syncState?.lastSuccessfulSyncAtMillis,
            lastError = syncState?.lastError,
        )
    }

    suspend fun replaceCache(
        fetchedCache: FetchedTaskCache,
        accountId: String = DEFAULT_ACCOUNT_ID,
    ) {
        database.withTransaction {
            database.taskDao().deleteAllTasks()
            database.taskListDao().deleteAllTaskLists()
            database.taskListDao().upsertTaskLists(fetchedCache.taskLists.map { it.toEntity() })
            database.taskDao().upsertTasks(fetchedCache.tasks.map { it.toEntity() })
            database.syncStateDao().upsertSyncState(
                SyncStateEntity(
                    accountId = accountId,
                    lastFullSyncAtMillis = fetchedCache.lastSuccessfulSyncAtMillis,
                    lastSuccessfulSyncAtMillis = fetchedCache.lastSuccessfulSyncAtMillis,
                    lastError = null,
                ),
            )
        }
    }

    suspend fun recordSyncError(
        accountId: String = DEFAULT_ACCOUNT_ID,
        message: String,
    ) {
        val existingState = database.syncStateDao().getSyncState(accountId)
        database.syncStateDao().upsertSyncState(
            SyncStateEntity(
                accountId = accountId,
                lastFullSyncAtMillis = existingState?.lastFullSyncAtMillis,
                lastSuccessfulSyncAtMillis = existingState?.lastSuccessfulSyncAtMillis,
                lastError = message,
            ),
        )
    }
}
