package com.example.taskreminder.data.repository

import androidx.room.withTransaction
import com.example.taskreminder.data.local.TASK_STATUS_COMPLETED
import com.example.taskreminder.data.local.SyncStateEntity
import com.example.taskreminder.data.local.TaskReminderDatabase
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
}
