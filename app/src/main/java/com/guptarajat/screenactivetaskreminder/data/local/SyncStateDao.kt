package com.guptarajat.screenactivetaskreminder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncStateDao {
    @Query("SELECT * FROM sync_state ORDER BY lastSuccessfulSyncAtMillis DESC LIMIT 1")
    fun observeLatestSyncState(): Flow<SyncStateEntity?>

    @Query("SELECT * FROM sync_state WHERE accountId = :accountId LIMIT 1")
    suspend fun getSyncState(accountId: String): SyncStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSyncState(syncState: SyncStateEntity)
}
