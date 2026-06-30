package com.pndnwngi.mysalesmanship.usecase

import com.pndnwngi.mysalesmanship.data.repository.VisitRepository
import javax.inject.Inject

class CloseVisitUseCase @Inject constructor(
    private val visitRepository: VisitRepository
) {
    suspend operator fun invoke(visitId: Long): Result<Unit> {
        val success = visitRepository.closeVisit(visitId, System.currentTimeMillis())
        return if (success) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Gagal menutup kunjungan"))
        }
    }
}
