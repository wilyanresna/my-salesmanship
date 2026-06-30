package com.pndnwngi.mysalesmanship.usecase

import com.pndnwngi.mysalesmanship.data.local.entity.TrCheckStockEntity
import com.pndnwngi.mysalesmanship.data.repository.CheckStockRepository
import javax.inject.Inject

class SaveCheckStockUseCase @Inject constructor(
    private val checkStockRepository: CheckStockRepository
) {
    suspend operator fun invoke(visitId: Long, productId: Long, stockQty: Int): Result<Long> {
        if (stockQty < 0) {
            return Result.failure(IllegalArgumentException("Kuantitas stok tidak boleh kurang dari 0"))
        }
        val entity = TrCheckStockEntity(
            trOutletId = visitId,
            productId = productId,
            stockQty = stockQty
        )
        val id = checkStockRepository.saveCheckStock(entity)
        return Result.success(id)
    }
}
