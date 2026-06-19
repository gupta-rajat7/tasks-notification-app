package com.guptarajat.screenactivetaskreminder.data.repository

import com.guptarajat.screenactivetaskreminder.data.local.TASK_STATUS_NEEDS_ACTION
import com.guptarajat.screenactivetaskreminder.data.local.TaskEntity
import com.guptarajat.screenactivetaskreminder.data.local.TaskListEntity

fun FetchedTaskList.toEntity(): TaskListEntity =
    TaskListEntity(
        id = id,
        title = title.ifBlank { "Untitled list" },
        updatedAtMillis = updatedAtMillis,
        isSelected = isSelected,
    )

fun FetchedTask.toEntity(): TaskEntity =
    TaskEntity(
        id = id,
        taskListId = taskListId,
        title = title.ifBlank { "Untitled task" },
        notes = notes,
        status = status.ifBlank { TASK_STATUS_NEEDS_ACTION },
        dueAtMillis = dueAtMillis,
        completedAtMillis = completedAtMillis,
        updatedAtMillis = updatedAtMillis,
        position = position,
    )
