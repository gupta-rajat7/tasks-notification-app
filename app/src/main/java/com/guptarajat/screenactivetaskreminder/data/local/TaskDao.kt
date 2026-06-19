package com.guptarajat.screenactivetaskreminder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query(
        """
        SELECT
            tasks.id AS id,
            tasks.taskListId AS taskListId,
            task_lists.title AS taskListTitle,
            tasks.title AS title,
            tasks.notes AS notes,
            tasks.status AS status,
            tasks.dueAtMillis AS dueAtMillis,
            tasks.completedAtMillis AS completedAtMillis,
            tasks.updatedAtMillis AS updatedAtMillis,
            tasks.position AS position
        FROM tasks
        INNER JOIN task_lists ON task_lists.id = tasks.taskListId
        WHERE task_lists.isSelected = 1
            AND tasks.status != :completedStatus
        ORDER BY
            CASE WHEN tasks.dueAtMillis IS NULL THEN 1 ELSE 0 END,
            tasks.dueAtMillis ASC,
            tasks.position ASC,
            tasks.title COLLATE NOCASE ASC
        """,
    )
    fun observePendingTasksForSelectedLists(completedStatus: String): Flow<List<CachedTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTasks(tasks: List<TaskEntity>)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
