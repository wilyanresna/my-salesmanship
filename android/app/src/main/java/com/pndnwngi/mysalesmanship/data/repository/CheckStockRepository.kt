package com.pndnwngi.mysalesmanship.data.repository

import com.pndnwngi.mysalesmanship.data.local.entity.TrCheckStockEntity

interface CheckStockRepository {
    suspend fun saveCheckStock(trCheckStock: TrCheckStockEntity): Long
    suspend fun getCheckStockByVisit(trOutletId: Long): List<TrCheckStockEntity>
}
