package com.guptarajat.screenactivetaskreminder.data.remote

import org.json.JSONArray
import org.json.JSONObject

internal object GoogleTasksJsonParser {
    fun parseTaskLists(json: String): GoogleTasksPage<GoogleTaskListDto> {
        val root = JSONObject(json)
        return GoogleTasksPage(
            items = root.optJSONArray("items").orEmpty().mapObjects { item ->
                val id = item.optionalString("id") ?: return@mapObjects null
                GoogleTaskListDto(
                    id = id,
                    title = item.optionalString("title").orEmpty(),
                    updated = item.optionalString("updated"),
                )
            },
            nextPageToken = root.optionalString("nextPageToken"),
        )
    }

    fun parseTasks(taskListId: String, json: String): GoogleTasksPage<GoogleTaskDto> {
        val root = JSONObject(json)
        return GoogleTasksPage(
            items = root.optJSONArray("items").orEmpty().mapObjects { item ->
                val id = item.optionalString("id") ?: return@mapObjects null
                GoogleTaskDto(
                    id = id,
                    taskListId = taskListId,
                    title = item.optionalString("title").orEmpty(),
                    notes = item.optionalString("notes"),
                    status = item.optionalString("status"),
                    due = item.optionalString("due"),
                    completed = item.optionalString("completed"),
                    updated = item.optionalString("updated"),
                    position = item.optionalString("position"),
                    deleted = item.optBoolean("deleted", false),
                )
            },
            nextPageToken = root.optionalString("nextPageToken"),
        )
    }
}

private fun JSONArray?.orEmpty(): JSONArray = this ?: JSONArray()

private inline fun <T> JSONArray.mapObjects(transform: (JSONObject) -> T?): List<T> {
    val values = mutableListOf<T>()
    for (index in 0 until length()) {
        val item = optJSONObject(index) ?: continue
        transform(item)?.let(values::add)
    }
    return values
}

private fun JSONObject.optionalString(name: String): String? {
    if (!has(name) || isNull(name)) {
        return null
    }
    return optString(name).takeIf { it.isNotBlank() }
}
