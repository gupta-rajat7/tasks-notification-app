package com.guptarajat.screenactivetaskreminder.data.repository

import com.guptarajat.screenactivetaskreminder.data.local.CachedTask

data class FetchedTaskCache(
    val taskLists: List<FetchedTaskList>,
    val tasks: List<FetchedTask>,
    val lastSuccessfulSyncAtMillis: Long,
)

data class FetchedTaskList(
    val id: String,
    val title: String,
    val updatedAtMillis: Long?,
    val isSelected: Boolean = true,
)

data class CachedTaskList(
    val id: String,
    val title: String,
    val updatedAtMillis: Long?,
    val isSelected: Boolean,
)

data class FetchedTask(
    val id: String,
    val taskListId: String,
    val title: String,
    val notes: String?,
    val status: String,
    val dueAtMillis: Long?,
    val completedAtMillis: Long?,
    val updatedAtMillis: Long?,
    val position: String?,
)

data class TaskCacheSnapshot(
    val pendingTasks: List<CachedTask> = emptyList(),
    val taskLists: List<CachedTaskList> = emptyList(),
    val selectedTaskListCount: Int = 0,
    val lastSuccessfulSyncAtMillis: Long? = null,
    val lastError: String? = null,
) {
    val pendingTaskCount: Int
        get() = pendingTasks.size

    val hasSyncedData: Boolean
        get() = lastSuccessfulSyncAtMillis != null || selectedTaskListCount > 0

    val taskListCount: Int
        get() = taskLists.size
}
