package com.pndnwngi.mysalesmanship.usecase

import com.pndnwngi.mysalesmanship.data.repository.OutletRepository
import com.pndnwngi.mysalesmanship.data.repository.VisitRepository
import javax.inject.Inject

class OpenVisitUseCase @Inject constructor(
    private val outletRepository: OutletRepository,
    private val visitRepository: VisitRepository
) {
    suspend fun openByBarcode(
        barcode: String,
        salesId: Long,
        visitType: String,
        lat: Double?,
        lng: Double?
    ): Result<Long> {
        val outlet = outletRepository.getOutletByBarcode(barcode)
            ?: return Result.failure(Exception("Outlet dengan barcode $barcode tidak ditemukan"))
        val visitId = visitRepository.startVisit(
            outletId = outlet.serverId,
            salesId = salesId,
            visitType = visitType,
            startTime = System.currentTimeMillis(),
            lat = lat,
            lng = lng
        )
        return Result.success(visitId)
    }

    suspend fun openById(
        outletId: Long,
        salesId: Long,
        visitType: String,
        lat: Double?,
        lng: Double?
    ): Result<Long> {
        val outlet = outletRepository.getOutletById(outletId)
            ?: return Result.failure(Exception("Outlet tidak ditemukan"))
        val visitId = visitRepository.startVisit(
            outletId = outlet.serverId,
            salesId = salesId,
            visitType = visitType,
            startTime = System.currentTimeMillis(),
            lat = lat,
            lng = lng
        )
        return Result.success(visitId)
    }
}
