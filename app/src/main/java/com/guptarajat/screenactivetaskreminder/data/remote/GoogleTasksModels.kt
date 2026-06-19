package com.guptarajat.screenactivetaskreminder.data.remote

internal data class GoogleTasksPage<T>(
    val items: List<T>,
    val nextPageToken: String?,
)

internal data class GoogleTaskListDto(
    val id: String,
    val title: String,
    val updated: String?,
)

internal data class GoogleTaskDto(
    val id: String,
    val taskListId: String,
    val title: String,
    val notes: String?,
    val status: String?,
    val due: String?,
    val completed: String?,
    val updated: String?,
    val position: String?,
    val deleted: Boolean,
)

internal data class GoogleTasksResponse(
    val taskLists: List<GoogleTaskListDto>,
    val tasks: List<GoogleTaskDto>,
)
