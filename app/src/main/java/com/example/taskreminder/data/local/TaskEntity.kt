package com.example.taskreminder.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

const val TASK_STATUS_COMPLETED = "completed"
const val TASK_STATUS_NEEDS_ACTION = "needsAction"

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskListEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskListId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["taskListId"]),
        Index(value = ["status"]),
        Index(value = ["dueAtMillis"]),
    ],
)
data class TaskEntity(
    @PrimaryKey val id: String,
    val taskListId: String,
    val title: String,
    val notes: String?,
    val status: String,
    val dueAtMillis: Long?,
    val completedAtMillis: Long?,
    val updatedAtMillis: Long?,
    val position: String?,
)
