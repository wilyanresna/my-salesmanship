package com.pndnwngi.mysalesmanship.data.repository

import com.pndnwngi.mysalesmanship.data.local.TokenManager
import com.pndnwngi.mysalesmanship.data.remote.ApiService
import com.pndnwngi.mysalesmanship.data.remote.ChangePasswordRequest
import com.pndnwngi.mysalesmanship.data.remote.LoginRequest
import com.pndnwngi.mysalesmanship.data.remote.LoginResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    tokenManager.saveTokens(body.accessToken, body.refreshToken)
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is empty"))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Login failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAccessToken(): Flow<String?> = tokenManager.accessToken

    override fun getRefreshToken(): Flow<String?> = tokenManager.refreshToken

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokenManager.saveTokens(accessToken, refreshToken)
    }

    override suspend fun clearTokens() {
        tokenManager.clearTokens()
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            val response = apiService.changePassword(ChangePasswordRequest(oldPassword, newPassword))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Ganti password gagal"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
