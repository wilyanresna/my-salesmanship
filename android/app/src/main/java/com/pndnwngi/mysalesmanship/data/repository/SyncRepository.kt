package com.pndnwngi.mysalesmanship.data.repository

import com.pndnwngi.mysalesmanship.data.local.entity.SyncQueueEntity

interface SyncRepository {
    suspend fun getPendingSyncItems(): List<SyncQueueEntity>
    suspend fun getFailedSyncItems(): List<SyncQueueEntity>
    suspend fun syncItem(syncItem: SyncQueueEntity): Result<Unit>
    suspend fun syncAllPending(): Result<Unit>
}
