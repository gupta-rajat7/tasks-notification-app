package com.guptarajat.screenactivetaskreminder.data.remote

import com.guptarajat.screenactivetaskreminder.data.repository.FetchedTaskCache
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

internal class GoogleTasksApiClient(
    private val baseUrl: String = "https://tasks.googleapis.com/tasks/v1",
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun fetchTaskCache(accessToken: String): GoogleTasksFetchResult =
        withContext(ioDispatcher) {
            runCatching {
                val taskLists = fetchTaskLists(accessToken).getOrThrow()
                val tasks = taskLists.flatMap { taskList ->
                    fetchTasksForList(
                        accessToken = accessToken,
                        taskListId = taskList.id,
                    ).getOrThrow()
                }
                GoogleTasksResponse(
                    taskLists = taskLists,
                    tasks = tasks,
                ).toFetchedTaskCache(syncAtMillis = System.currentTimeMillis())
            }.fold(
                onSuccess = { GoogleTasksFetchResult.Success(it) },
                onFailure = {
                    GoogleTasksFetchResult.Failure(
                        it.message ?: "Google Tasks sync failed.",
                    )
                },
            )
        }

    private fun fetchTaskLists(accessToken: String): Result<List<GoogleTaskListDto>> {
        val taskLists = mutableListOf<GoogleTaskListDto>()
        var pageToken: String? = null
        do {
            val response = getJson(
                accessToken = accessToken,
                path = "/users/@me/lists",
                query = mapOf(
                    "maxResults" to "1000",
                    "pageToken" to pageToken,
                ),
            ).getOrElse { return Result.failure(it) }
            val page = GoogleTasksJsonParser.parseTaskLists(response)
            taskLists += page.items
            pageToken = page.nextPageToken
        } while (!pageToken.isNullOrBlank())
        return Result.success(taskLists)
    }

    private fun fetchTasksForList(
        accessToken: String,
        taskListId: String,
    ): Result<List<GoogleTaskDto>> {
        val tasks = mutableListOf<GoogleTaskDto>()
        var pageToken: String? = null
        do {
            val response = getJson(
                accessToken = accessToken,
                path = "/lists/${taskListId.urlEncode()}/tasks",
                query = mapOf(
                    "maxResults" to "100",
                    "showCompleted" to "false",
                    "showDeleted" to "false",
                    "showHidden" to "false",
                    "pageToken" to pageToken,
                ),
            ).getOrElse { return Result.failure(it) }
            val page = GoogleTasksJsonParser.parseTasks(
                taskListId = taskListId,
                json = response,
            )
            tasks += page.items
            pageToken = page.nextPageToken
        } while (!pageToken.isNullOrBlank())
        return Result.success(tasks)
    }

    private fun getJson(
        accessToken: String,
        path: String,
        query: Map<String, String?>,
    ): Result<String> {
        val url = URL(buildUrl(path, query))
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 15_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Authorization", "Bearer $accessToken")
        }

        return try {
            val responseCode = connection.responseCode
            val body = connection.readResponseBody(responseCode)
            if (responseCode in 200..299) {
                Result.success(body)
            } else {
                Result.failure(
                    IOException(
                        "Google Tasks returned HTTP $responseCode: ${body.googleErrorMessage()}",
                    ),
                )
            }
        } catch (error: IOException) {
            Result.failure(error)
        } finally {
            connection.disconnect()
        }
    }

    private fun buildUrl(path: String, query: Map<String, String?>): String {
        val queryString = query
            .mapNotNull { (key, value) ->
                value?.takeIf { it.isNotBlank() }?.let {
                    "${key.urlEncode()}=${it.urlEncode()}"
                }
            }
            .joinToString("&")

        return if (queryString.isBlank()) {
            "$baseUrl$path"
        } else {
            "$baseUrl$path?$queryString"
        }
    }
}

internal sealed interface GoogleTasksFetchResult {
    data class Success(val cache: FetchedTaskCache) : GoogleTasksFetchResult
    data class Failure(val message: String) : GoogleTasksFetchResult
}

private fun HttpURLConnection.readResponseBody(responseCode: Int): String {
    val stream = if (responseCode in 200..299) {
        inputStream
    } else {
        errorStream ?: inputStream
    }
    return stream.bufferedReader().use { it.readText() }
}

private fun String.googleErrorMessage(): String =
    runCatching {
        JSONObject(this)
            .optJSONObject("error")
            ?.optString("message")
            ?.takeIf { it.isNotBlank() }
    }.getOrNull() ?: "No error details were returned."

private fun String.urlEncode(): String =
    URLEncoder.encode(this, Charsets.UTF_8.name()).replace("+", "%20")
