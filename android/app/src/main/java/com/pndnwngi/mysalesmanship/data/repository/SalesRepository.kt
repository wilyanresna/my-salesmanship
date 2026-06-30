package com.pndnwngi.mysalesmanship.data.repository

import com.pndnwngi.mysalesmanship.data.local.entity.TrSalesDetailEntity
import com.pndnwngi.mysalesmanship.data.local.entity.TrSalesEntity

interface SalesRepository {
    suspend fun saveSales(sales: TrSalesEntity, details: List<TrSalesDetailEntity>): Long
    suspend fun getSalesByVisit(trOutletId: Long): List<TrSalesEntity>
    suspend fun getDetailsBySalesId(trSalesId: Long): List<TrSalesDetailEntity>
    suspend fun getRemainingStock(productId: Long): Int
    suspend fun getRemainingStockUom(productId: Long): UomQty
}

data class UomQty(val dus: Int, val bal: Int, val slf: Int, val bks: Int)
