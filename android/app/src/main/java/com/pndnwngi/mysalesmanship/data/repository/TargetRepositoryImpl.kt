package com.pndnwngi.mysalesmanship.data.repository

import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TargetRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : TargetRepository {

    override suspend fun getTargetsForRoute(routeId: Long, date: String): List<ProductAchievement> = withContext(Dispatchers.IO) {
        val targets = database.salesTargetDao().getTargetsByRouteAndDate(routeId, date)
        targets.map { target ->
            val product = database.mstProductDao().getProductByServerId(target.productId)
            val productName = product?.name ?: "Unknown Product"
            val sold = database.trSalesDetailDao().getTotalSoldByProduct(target.productId) ?: 0
            ProductAchievement(
                productId = target.productId,
                productName = productName,
                soldQty = sold,
                targetQty = target.targetQty
            )
        }
    }

    override suspend fun getAllAchievements(): List<ProductAchievement> = withContext(Dispatchers.IO) {
        val targets = database.salesTargetDao().getAllTargets()
        targets.map { target ->
            val product = database.mstProductDao().getProductByServerId(target.productId)
            val productName = product?.name ?: "Unknown Product"
            val sold = database.trSalesDetailDao().getTotalSoldByProduct(target.productId) ?: 0
            ProductAchievement(
                productId = target.productId,
                productName = productName,
                soldQty = sold,
                targetQty = target.targetQty
            )
        }
    }
}
