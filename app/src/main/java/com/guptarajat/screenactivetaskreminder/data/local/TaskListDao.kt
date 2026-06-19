package com.guptarajat.screenactivetaskreminder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskListDao {
    @Query("SELECT * FROM task_lists ORDER BY title COLLATE NOCASE")
    fun observeTaskLists(): Flow<List<TaskListEntity>>

    @Query("SELECT COUNT(*) FROM task_lists WHERE isSelected = 1")
    fun observeSelectedTaskListCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTaskLists(taskLists: List<TaskListEntity>)

    @Query("DELETE FROM task_lists")
    suspend fun deleteAllTaskLists()
}
