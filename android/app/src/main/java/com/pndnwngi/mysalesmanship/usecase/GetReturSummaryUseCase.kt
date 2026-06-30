package com.pndnwngi.mysalesmanship.usecase

import com.pndnwngi.mysalesmanship.data.repository.ProductRemainingStock
import com.pndnwngi.mysalesmanship.data.repository.StockRepository
import javax.inject.Inject

class GetReturSummaryUseCase @Inject constructor(
    private val stockRepository: StockRepository
) {
    suspend operator fun invoke(): Result<List<ProductRemainingStock>> {
        return try {
            val list = stockRepository.getRemainingStockList()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
