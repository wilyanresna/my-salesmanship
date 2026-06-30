package com.pndnwngi.mysalesmanship.usecase

import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import com.pndnwngi.mysalesmanship.data.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

data class DashboardOutlet(
    val id: String,
    val serverId: Long,
    val name: String,
    val address: String,
    val isVisited: Boolean,
    val routeName: String,
    val routeId: Long
)

data class DashboardData(
    val salesName: String,
    val salesId: String,
    val weekPeriod: String,
    val stocks: List<ProductRemainingStock>,
    val achievements: List<ProductAchievement>,
    val outlets: List<DashboardOutlet>
)

class GetDashboardUseCase @Inject constructor(
    private val database: AppDatabase,
    private val stockRepository: StockRepository,
    private val targetRepository: TargetRepository,
    private val outletRepository: OutletRepository,
    private val visitRepository: VisitRepository
) {
    suspend operator fun invoke(): Result<DashboardData> = withContext(Dispatchers.IO) {
        try {
            val salesman = database.mstSalesmanDao().getSalesman()
                ?: return@withContext Result.failure(Exception("Data salesman belum ditarik"))

            val activeStock = stockRepository.getActiveStock()
            val weekPeriod = if (activeStock != null) {
                val target = database.salesTargetDao().getAllTargets().firstOrNull()
                if (target != null) {
                    "${target.weekStart} s/d ${target.weekEnd}"
                } else {
                    activeStock.dateUsed
                }
            } else {
                "Belum ada data aktif"
            }

            val stocks = stockRepository.getRemainingStockList()
            val achievements = targetRepository.getAllAchievements()
            val outlets = outletRepository.getAllOutlets()

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endOfDay = calendar.timeInMillis

            val dashboardOutlets = outlets.map { outlet ->
                val visits = visitRepository.getVisitsByOutletAndDate(outlet.serverId, startOfDay, endOfDay)
                val isVisited = visits.any { it.status == "CLOSED" }

                val routeName = when (outlet.routeId) {
                    1L -> "Rute Senin (Route 1)"
                    2L -> "Rute Selasa (Route 2)"
                    3L -> "Rute Rabu (Route 3)"
                    4L -> "Rute Kamis (Route 4)"
                    5L -> "Rute Jumat (Route 5)"
                    6L -> "Rute Sabtu (Route 6)"
                    7L -> "Rute Minggu (Route 7)"
                    else -> "Rute #${outlet.routeId}"
                }

                DashboardOutlet(
                    id = "OUT-${outlet.serverId}",
                    serverId = outlet.serverId,
                    name = outlet.name,
                    address = outlet.ownerName ?: "",
                    isVisited = isVisited,
                    routeName = routeName,
                    routeId = outlet.routeId
                )
            }

            val data = DashboardData(
                salesName = salesman.salesName,
                salesId = "SLS-${salesman.salesId}",
                weekPeriod = weekPeriod,
                stocks = stocks,
                achievements = achievements,
                outlets = dashboardOutlets
            )
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
