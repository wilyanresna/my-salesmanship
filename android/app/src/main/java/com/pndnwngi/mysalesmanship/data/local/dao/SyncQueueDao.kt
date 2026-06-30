package com.pndnwngi.mysalesmanship.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pndnwngi.mysalesmanship.data.local.entity.SyncQueueEntity

@Dao
interface SyncQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(syncQueue: SyncQueueEntity): Long

    @Query("UPDATE sync_queue SET status = :status WHERE id = :id")
    fun updateStatus(id: Long, status: String): Int

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING'")
    fun getPendingItems(): List<SyncQueueEntity>

    @Query("SELECT * FROM sync_queue WHERE status = 'FAILED'")
    fun getFailedItems(): List<SyncQueueEntity>

    @Query("DELETE FROM sync_queue WHERE status = 'SYNCED'")
    fun deleteSyncedItems(): Int
}
