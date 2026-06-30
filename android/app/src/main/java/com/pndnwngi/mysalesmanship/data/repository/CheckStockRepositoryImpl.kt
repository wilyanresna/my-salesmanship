package com.pndnwngi.mysalesmanship.data.repository

import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import com.pndnwngi.mysalesmanship.data.local.entity.TrCheckStockEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckStockRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : CheckStockRepository {

    override suspend fun saveCheckStock(trCheckStock: TrCheckStockEntity): Long = withContext(Dispatchers.IO) {
        database.trCheckStockDao().insert(trCheckStock)
    }

    override suspend fun getCheckStockByVisit(trOutletId: Long): List<TrCheckStockEntity> = withContext(Dispatchers.IO) {
        database.trCheckStockDao().getCheckStockByVisit(trOutletId)
    }
}
