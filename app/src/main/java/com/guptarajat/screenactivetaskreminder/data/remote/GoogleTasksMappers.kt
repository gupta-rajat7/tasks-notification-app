package com.guptarajat.screenactivetaskreminder.data.remote

import com.guptarajat.screenactivetaskreminder.data.local.TASK_STATUS_COMPLETED
import com.guptarajat.screenactivetaskreminder.data.local.TASK_STATUS_NEEDS_ACTION
import com.guptarajat.screenactivetaskreminder.data.repository.FetchedTask
import com.guptarajat.screenactivetaskreminder.data.repository.FetchedTaskCache
import com.guptarajat.screenactivetaskreminder.data.repository.FetchedTaskList
import java.time.Instant
import java.time.format.DateTimeParseException

internal fun GoogleTasksResponse.toFetchedTaskCache(syncAtMillis: Long): FetchedTaskCache =
    FetchedTaskCache(
        taskLists = taskLists.map { it.toFetchedTaskList() },
        tasks = tasks
            .filterNot { task ->
                task.deleted || task.status == TASK_STATUS_COMPLETED
            }
            .map { it.toFetchedTask() },
        lastSuccessfulSyncAtMillis = syncAtMillis,
    )

private fun GoogleTaskListDto.toFetchedTaskList(): FetchedTaskList =
    FetchedTaskList(
        id = id,
        title = title,
        updatedAtMillis = updated.toEpochMillisOrNull(),
        isSelected = true,
    )

private fun GoogleTaskDto.toFetchedTask(): FetchedTask =
    FetchedTask(
        id = id,
        taskListId = taskListId,
        title = title,
        notes = notes,
        status = status ?: TASK_STATUS_NEEDS_ACTION,
        dueAtMillis = due.toEpochMillisOrNull(),
        completedAtMillis = completed.toEpochMillisOrNull(),
        updatedAtMillis = updated.toEpochMillisOrNull(),
        position = position,
    )

private fun String?.toEpochMillisOrNull(): Long? {
    if (isNullOrBlank()) {
        return null
    }
    return try {
        Instant.parse(this).toEpochMilli()
    } catch (error: DateTimeParseException) {
        null
    }
}
