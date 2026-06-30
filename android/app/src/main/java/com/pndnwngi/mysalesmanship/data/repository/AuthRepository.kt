package com.pndnwngi.mysalesmanship.data.repository

import com.pndnwngi.mysalesmanship.data.remote.LoginResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoginResponse>
    fun getAccessToken(): Flow<String?>
    fun getRefreshToken(): Flow<String?>
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>
}
