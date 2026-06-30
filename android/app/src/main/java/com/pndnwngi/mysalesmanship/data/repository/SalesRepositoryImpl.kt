package com.pndnwngi.mysalesmanship.data.repository

import androidx.room.withTransaction
import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import com.pndnwngi.mysalesmanship.data.local.entity.TrSalesDetailEntity
import com.pndnwngi.mysalesmanship.data.local.entity.TrSalesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalesRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : SalesRepository {

    override suspend fun saveSales(
        sales: TrSalesEntity,
        details: List<TrSalesDetailEntity>
    ): Long = withContext(Dispatchers.IO) {
        database.withTransaction {
            val salesId = database.trSalesDao().insert(sales)
            val detailsWithSalesId = details.map { it.copy(trSalesId = salesId) }
            database.trSalesDetailDao().insertAll(detailsWithSalesId)
            salesId
        }
    }

    override suspend fun getSalesByVisit(trOutletId: Long): List<TrSalesEntity> = withContext(Dispatchers.IO) {
        database.trSalesDao().getSalesByVisit(trOutletId)
    }

    override suspend fun getDetailsBySalesId(trSalesId: Long): List<TrSalesDetailEntity> = withContext(Dispatchers.IO) {
        database.trSalesDetailDao().getDetailsBySalesId(trSalesId)
    }

    override suspend fun getRemainingStock(productId: Long): Int = withContext(Dispatchers.IO) {
        val activeStock = database.stockRokokDao().getActiveStock() ?: return@withContext 0
        val stockItem = database.stockRokokItemDao().getItemByStockAndProduct(activeStock.id, productId) ?: return@withContext 0
        val product = database.mstProductDao().getProductByServerId(productId) ?: return@withContext 0

        val totalBal = stockItem.qtyDusInit * product.uomBal + stockItem.qtyBalInit
        val totalSlf = totalBal * product.uomSlf + stockItem.qtySlfInit
        val initialBungkus = totalSlf * product.uomBks + stockItem.qtyBksInit

        val totalSold = database.trSalesDetailDao().getTotalSoldByProduct(productId) ?: 0

        val remaining = initialBungkus - totalSold
        if (remaining < 0) 0 else remaining
    }

    override suspend fun getRemainingStockUom(productId: Long): UomQty = withContext(Dispatchers.IO) {
        val remainingBungkus = getRemainingStock(productId)
        val product = database.mstProductDao().getProductByServerId(productId) ?: return@withContext UomQty(0, 0, 0, 0)

        var remaining = remainingBungkus
        val bksPerSlf = product.uomBks
        val bksPerBal = product.uomSlf * product.uomBks
        val bksPerDus = product.uomBal * product.uomSlf * product.uomBks

        val dus = remaining / bksPerDus
        remaining %= bksPerDus

        val bal = remaining / bksPerBal
        remaining %= bksPerBal

        val slf = remaining / bksPerSlf
        remaining %= bksPerSlf

        val bks = remaining

        UomQty(dus, bal, slf, bks)
    }
}
