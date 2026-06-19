package com.guptarajat.screenactivetaskreminder.data.remote

import com.guptarajat.screenactivetaskreminder.data.local.TASK_STATUS_COMPLETED
import com.guptarajat.screenactivetaskreminder.data.local.TASK_STATUS_NEEDS_ACTION
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class GoogleTasksMappersTest {
    @Test
    fun mapsGoogleTasksResponseToFetchedCache() {
        val cache = GoogleTasksResponse(
            taskLists = listOf(
                GoogleTaskListDto(
                    id = "list-1",
                    title = "Inbox",
                    updated = "2026-06-19T08:00:00.000Z",
                ),
            ),
            tasks = listOf(
                GoogleTaskDto(
                    id = "task-1",
                    taskListId = "list-1",
                    title = "Review proposal",
                    notes = "Bring notes to the next planning pass.",
                    status = TASK_STATUS_NEEDS_ACTION,
                    due = "2026-06-20T00:00:00.000Z",
                    completed = null,
                    updated = "2026-06-19T09:00:00.000Z",
                    position = "0001",
                    deleted = false,
                ),
            ),
        ).toFetchedTaskCache(syncAtMillis = 123L)

        assertEquals(123L, cache.lastSuccessfulSyncAtMillis)
        assertEquals(1, cache.taskLists.size)
        assertEquals("Inbox", cache.taskLists.single().title)
        assertEquals(
            Instant.parse("2026-06-19T08:00:00.000Z").toEpochMilli(),
            cache.taskLists.single().updatedAtMillis,
        )
        assertEquals(1, cache.tasks.size)
        assertEquals("Review proposal", cache.tasks.single().title)
        assertEquals(
            Instant.parse("2026-06-20T00:00:00.000Z").toEpochMilli(),
            cache.tasks.single().dueAtMillis,
        )
    }

    @Test
    fun filtersCompletedAndDeletedTasks() {
        val cache = GoogleTasksResponse(
            taskLists = emptyList(),
            tasks = listOf(
                GoogleTaskDto(
                    id = "pending-task",
                    taskListId = "list-1",
                    title = "Keep me",
                    notes = null,
                    status = null,
                    due = null,
                    completed = null,
                    updated = "not-a-date",
                    position = null,
                    deleted = false,
                ),
                GoogleTaskDto(
                    id = "completed-task",
                    taskListId = "list-1",
                    title = "Drop me",
                    notes = null,
                    status = TASK_STATUS_COMPLETED,
                    due = null,
                    completed = "2026-06-19T09:30:00.000Z",
                    updated = null,
                    position = null,
                    deleted = false,
                ),
                GoogleTaskDto(
                    id = "deleted-task",
                    taskListId = "list-1",
                    title = "Drop me too",
                    notes = null,
                    status = TASK_STATUS_NEEDS_ACTION,
                    due = null,
                    completed = null,
                    updated = null,
                    position = null,
                    deleted = true,
                ),
            ),
        ).toFetchedTaskCache(syncAtMillis = 456L)

        assertEquals(1, cache.tasks.size)
        assertEquals("pending-task", cache.tasks.single().id)
        assertEquals(TASK_STATUS_NEEDS_ACTION, cache.tasks.single().status)
        assertNull(cache.tasks.single().updatedAtMillis)
    }
}
