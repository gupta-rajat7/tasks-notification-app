package com.guptarajat.screenactivetaskreminder.data.repository

import com.guptarajat.screenactivetaskreminder.data.local.TASK_STATUS_NEEDS_ACTION
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskCacheMappersTest {
    @Test
    fun fetchedTaskListMapsToRoomEntity() {
        val entity = FetchedTaskList(
            id = "list-1",
            title = "Inbox",
            updatedAtMillis = 100L,
            isSelected = true,
        ).toEntity()

        assertEquals("list-1", entity.id)
        assertEquals("Inbox", entity.title)
        assertEquals(100L, entity.updatedAtMillis)
        assertTrue(entity.isSelected)
    }

    @Test
    fun blankTaskTitleAndStatusUseSafeDefaults() {
        val entity = FetchedTask(
            id = "task-1",
            taskListId = "list-1",
            title = "",
            notes = null,
            status = "",
            dueAtMillis = null,
            completedAtMillis = null,
            updatedAtMillis = null,
            position = "1",
        ).toEntity()

        assertEquals("Untitled task", entity.title)
        assertEquals(TASK_STATUS_NEEDS_ACTION, entity.status)
    }

    @Test
    fun snapshotExposesPendingCountAndSyncedState() {
        val emptySnapshot = TaskCacheSnapshot()
        val syncedSnapshot = TaskCacheSnapshot(selectedTaskListCount = 1)

        assertEquals(0, emptySnapshot.pendingTaskCount)
        assertEquals(false, emptySnapshot.hasSyncedData)
        assertEquals(true, syncedSnapshot.hasSyncedData)
    }
}
