package com.guptarajat.screenactivetaskreminder.data.local

data class CachedTask(
    val id: String,
    val taskListId: String,
    val taskListTitle: String,
    val title: String,
    val notes: String?,
    val status: String,
    val dueAtMillis: Long?,
    val completedAtMillis: Long?,
    val updatedAtMillis: Long?,
    val position: String?,
)
