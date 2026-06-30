package com.pndnwngi.mysalesmanship.data.repository

data class ProductAchievement(
    val productId: Long,
    val productName: String,
    val soldQty: Int,
    val targetQty: Int
)

interface TargetRepository {
    suspend fun getTargetsForRoute(routeId: Long, date: String): List<ProductAchievement>
    suspend fun getAllAchievements(): List<ProductAchievement>
}
