package com.pndnwngi.mysalesmanship.data.repository

import androidx.room.withTransaction
import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import com.pndnwngi.mysalesmanship.data.local.entity.SyncQueueEntity
import com.pndnwngi.mysalesmanship.data.local.entity.TrOutletEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : VisitRepository {

    override suspend fun getVisitById(id: Long): TrOutletEntity? = withContext(Dispatchers.IO) {
        database.trOutletDao().getVisitById(id)
    }

    override suspend fun getOpenVisits(): List<TrOutletEntity> = withContext(Dispatchers.IO) {
        database.trOutletDao().getAllOpenVisits()
    }

    override suspend fun getVisitsByOutletAndDate(
        outletId: Long,
        startOfDay: Long,
        endOfDay: Long
    ): List<TrOutletEntity> = withContext(Dispatchers.IO) {
        database.trOutletDao().getVisitsByOutletAndDate(outletId, startOfDay, endOfDay)
    }

    override suspend fun startVisit(
        outletId: Long,
        salesId: Long,
        visitType: String,
        startTime: Long,
        lat: Double?,
        lng: Double?
    ): Long = withContext(Dispatchers.IO) {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = startTime
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        val todayVisits = database.trOutletDao().getVisitsByOutletAndDate(outletId, startOfDay, endOfDay)
        val visitNo = todayVisits.size + 1

        val entity = TrOutletEntity(
            outletId = outletId,
            salesId = salesId,
            visitNo = visitNo,
            visitType = visitType,
            status = "OPEN",
            startTime = startTime,
            endTime = null,
            lat = lat,
            lng = lng
        )
        database.trOutletDao().insert(entity)
    }

    override suspend fun closeVisit(visitId: Long, endTime: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            database.withTransaction {
                val updatedRows = database.trOutletDao().updateStatusAndEndTime(visitId, "CLOSED", endTime)
                if (updatedRows > 0) {
                    val batchId = UUID.randomUUID().toString()
                    val syncItem = SyncQueueEntity(
                        entityType = "VISIT",
                        entityId = visitId,
                        batchId = batchId,
                        status = "PENDING"
                    )
                    database.syncQueueDao().insert(syncItem)
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            false
        }
    }
}
