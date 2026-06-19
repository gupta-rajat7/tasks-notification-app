package com.example.taskreminder.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TaskListEntity::class,
        TaskEntity::class,
        SyncStateEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class TaskReminderDatabase : RoomDatabase() {
    abstract fun taskListDao(): TaskListDao
    abstract fun taskDao(): TaskDao
    abstract fun syncStateDao(): SyncStateDao

    companion object {
        @Volatile
        private var INSTANCE: TaskReminderDatabase? = null

        fun getInstance(context: Context): TaskReminderDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TaskReminderDatabase::class.java,
                    "task_reminder.db",
                ).build().also { INSTANCE = it }
            }
    }
}
