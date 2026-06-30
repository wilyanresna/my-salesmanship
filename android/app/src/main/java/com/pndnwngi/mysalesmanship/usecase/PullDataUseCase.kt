package com.pndnwngi.mysalesmanship.usecase

import com.pndnwngi.mysalesmanship.data.repository.PullRepository
import javax.inject.Inject

class PullDataUseCase @Inject constructor(
    private val pullRepository: PullRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return pullRepository.pullData()
    }
}
