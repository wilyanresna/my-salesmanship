package com.pndnwngi.mysalesmanship.data.repository

import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import com.pndnwngi.mysalesmanship.data.local.entity.StockRokokEntity
import com.pndnwngi.mysalesmanship.data.local.entity.StockRokokItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : StockRepository {

    override suspend fun getActiveStock(): StockRokokEntity? = withContext(Dispatchers.IO) {
        database.stockRokokDao().getActiveStock()
    }

    override suspend fun getStockItems(stockRokokId: Long): List<StockRokokItemEntity> = withContext(Dispatchers.IO) {
        database.stockRokokItemDao().getItemsByStockRokokId(stockRokokId)
    }

    override suspend fun getRemainingStockList(): List<ProductRemainingStock> = withContext(Dispatchers.IO) {
        val activeStock = database.stockRokokDao().getActiveStock() ?: return@withContext emptyList()
        val items = database.stockRokokItemDao().getItemsByStockRokokId(activeStock.id)
        
        items.mapNotNull { item ->
            val product = database.mstProductDao().getProductByServerId(item.productId) ?: return@mapNotNull null
            
            val totalBal = item.qtyDusInit * product.uomBal + item.qtyBalInit
            val totalSlf = totalBal * product.uomSlf + item.qtySlfInit
            val initialBungkus = totalSlf * product.uomBks + item.qtyBksInit

            val totalSold = database.trSalesDetailDao().getTotalSoldByProduct(item.productId) ?: 0
            
            val remainingBungkus = if (initialBungkus - totalSold < 0) 0 else initialBungkus - totalSold
            
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

            ProductRemainingStock(
                productId = item.productId,
                name = product.name,
                sku = product.sku,
                qtyDusInit = item.qtyDusInit,
                qtyBalInit = item.qtyBalInit,
                qtySlfInit = item.qtySlfInit,
                qtyBksInit = item.qtyBksInit,
                qtyDusRemaining = dus,
                qtyBalRemaining = bal,
                qtySlfRemaining = slf,
                qtyBksRemaining = bks
            )
        }
    }
}
