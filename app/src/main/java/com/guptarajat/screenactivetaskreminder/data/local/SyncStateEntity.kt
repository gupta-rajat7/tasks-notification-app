package com.guptarajat.screenactivetaskreminder.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_state")
data class SyncStateEntity(
    @PrimaryKey val accountId: String,
    val lastFullSyncAtMillis: Long?,
    val lastSuccessfulSyncAtMillis: Long?,
    val lastError: String?,
)
