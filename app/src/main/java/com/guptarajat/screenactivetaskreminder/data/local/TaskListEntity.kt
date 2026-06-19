package com.guptarajat.screenactivetaskreminder.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_lists")
data class TaskListEntity(
    @PrimaryKey val id: String,
    val title: String,
    val updatedAtMillis: Long?,
    val isSelected: Boolean,
)
