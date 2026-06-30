package com.pndnwngi.mysalesmanship.data.repository

import com.pndnwngi.mysalesmanship.data.local.entity.TrOutletEntity

interface VisitRepository {
    suspend fun getVisitById(id: Long): TrOutletEntity?
    suspend fun getOpenVisits(): List<TrOutletEntity>
    suspend fun getVisitsByOutletAndDate(outletId: Long, startOfDay: Long, endOfDay: Long): List<TrOutletEntity>
    suspend fun startVisit(
        outletId: Long,
        salesId: Long,
        visitType: String,
        startTime: Long,
        lat: Double?,
        lng: Double?
    ): Long
    suspend fun closeVisit(visitId: Long, endTime: Long): Boolean
}
