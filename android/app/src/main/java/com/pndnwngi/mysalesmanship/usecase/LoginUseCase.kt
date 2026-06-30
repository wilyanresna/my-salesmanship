package com.pndnwngi.mysalesmanship.usecase

import com.pndnwngi.mysalesmanship.data.remote.LoginResponse
import com.pndnwngi.mysalesmanship.data.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<LoginResponse> {
        if (username.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Username dan password tidak boleh kosong"))
        }
        return authRepository.login(username, password)
    }
}
