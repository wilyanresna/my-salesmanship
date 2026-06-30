package com.pndnwngi.mysalesmanship.data.repository

import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import com.pndnwngi.mysalesmanship.data.local.entity.SyncQueueEntity
import com.pndnwngi.mysalesmanship.data.remote.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val database: AppDatabase
) : SyncRepository {

    override suspend fun getPendingSyncItems(): List<SyncQueueEntity> = withContext(Dispatchers.IO) {
        database.syncQueueDao().getPendingItems()
    }

    override suspend fun getFailedSyncItems(): List<SyncQueueEntity> = withContext(Dispatchers.IO) {
        database.syncQueueDao().getFailedItems()
    }

    override suspend fun syncItem(syncItem: SyncQueueEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val visit = database.trOutletDao().getVisitById(syncItem.entityId)
                ?: return@withContext Result.failure(Exception("Visit not found for ID: ${syncItem.entityId}"))

            val checkStocks = database.trCheckStockDao().getCheckStockByVisit(visit.id)
            val uploadCheckStocks = checkStocks.map {
                UploadCheckStockRequest(
                    productId = it.productId,
                    stockQty = it.stockQty
                )
            }

            val salesList = database.trSalesDao().getSalesByVisit(visit.id)
            val uploadSales = salesList.map { sales ->
                val details = database.trSalesDetailDao().getDetailsBySalesId(sales.id)
                UploadSalesRequest(
                    salesOrder = sales.salesOrder,
                    details = details.map { detail ->
                        UploadSalesDetailRequest(
                            productId = detail.productId,
                            qty = detail.qty,
                            price = detail.price,
                            total = detail.total
                        )
                    }
                )
            }

            val startTimeStr = Instant.ofEpochMilli(visit.startTime).toString()
            val endTimeStr = visit.endTime?.let { Instant.ofEpochMilli(it).toString() }

            val uploadVisitRequest = UploadVisitRequest(
                outletId = visit.outletId,
                visitNo = visit.visitNo,
                visitType = visit.visitType,
                status = visit.status,
                startTime = startTimeStr,
                endTime = endTimeStr,
                lat = visit.lat,
                lng = visit.lng,
                checkStocks = uploadCheckStocks,
                sales = uploadSales
            )

            val payload = UploadPayload(
                batchId = syncItem.batchId,
                visit = uploadVisitRequest
            )

            val response = apiService.uploadVisit(payload)
            if (response.isSuccessful || response.code() == 409) {
                database.syncQueueDao().updateStatus(syncItem.id, "SYNCED")
                database.syncQueueDao().deleteSyncedItems()
                Result.success(Unit)
            } else {
                val errMsg = response.errorBody()?.string() ?: "Upload failed with status code ${response.code()}"
                database.syncQueueDao().updateStatus(syncItem.id, "FAILED")
                Result.failure(Exception(errMsg))
            }
        } catch (e: Exception) {
            database.syncQueueDao().updateStatus(syncItem.id, "FAILED")
            Result.failure(e)
        }
    }

    override suspend fun syncAllPending(): Result<Unit> = withContext(Dispatchers.IO) {
        val pending = database.syncQueueDao().getPendingItems() + database.syncQueueDao().getFailedItems()
        var hasError = false
        var lastError: Throwable? = null

        for (item in pending) {
            database.syncQueueDao().updateStatus(item.id, "SYNCING")
            val res = syncItem(item)
            if (res.isFailure) {
                hasError = true
                lastError = res.exceptionOrNull()
            }
        }

        if (hasError) {
            Result.failure(lastError ?: Exception("Some items failed to sync"))
        } else {
            Result.success(Unit)
        }
    }
}
