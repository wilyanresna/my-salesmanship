package com.pndnwngi.mysalesmanship.data.repository

import com.pndnwngi.mysalesmanship.data.local.entity.StockRokokEntity
import com.pndnwngi.mysalesmanship.data.local.entity.StockRokokItemEntity

data class ProductRemainingStock(
    val productId: Long,
    val name: String,
    val sku: String,
    val qtyDusInit: Int,
    val qtyBalInit: Int,
    val qtySlfInit: Int,
    val qtyBksInit: Int,
    val qtyDusRemaining: Int,
    val qtyBalRemaining: Int,
    val qtySlfRemaining: Int,
    val qtyBksRemaining: Int
)

interface StockRepository {
    suspend fun getActiveStock(): StockRokokEntity?
    suspend fun getStockItems(stockRokokId: Long): List<StockRokokItemEntity>
    suspend fun getRemainingStockList(): List<ProductRemainingStock>
}
