package com.pndnwngi.mysalesmanship.usecase

import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import com.pndnwngi.mysalesmanship.data.local.entity.TrSalesDetailEntity
import com.pndnwngi.mysalesmanship.data.local.entity.TrSalesEntity
import com.pndnwngi.mysalesmanship.data.repository.SalesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class SalesItemInput(
    val productId: Long,
    val qty: Int,
    val price: Double
)

class AddSalesUseCase @Inject constructor(
    private val salesRepository: SalesRepository,
    private val database: AppDatabase
) {
    suspend operator fun invoke(visitId: Long, items: List<SalesItemInput>): Result<Long> = withContext(Dispatchers.IO) {
        if (items.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Item penjualan tidak boleh kosong"))
        }

        for (item in items) {
            val remaining = salesRepository.getRemainingStock(item.productId)
            if (item.qty > remaining) {
                val product = database.mstProductDao().getProductByServerId(item.productId)
                val productName = product?.name ?: "Produk #${item.productId}"
                return@withContext Result.failure(Exception("Stok tidak mencukupi untuk $productName. Sisa stok: $remaining bungkus"))
            }
        }

        val salesOrder = "SO-${System.currentTimeMillis()}"
        val salesEntity = TrSalesEntity(
            trOutletId = visitId,
            salesOrder = salesOrder
        )

        val detailEntities = items.map { item ->
            TrSalesDetailEntity(
                trSalesId = 0L,
                productId = item.productId,
                qty = item.qty,
                price = item.price,
                total = item.qty * item.price
            )
        }

        val salesId = salesRepository.saveSales(salesEntity, detailEntities)
        Result.success(salesId)
    }
}
