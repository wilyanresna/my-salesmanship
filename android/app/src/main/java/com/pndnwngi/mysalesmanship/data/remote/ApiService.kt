package com.pndnwngi.mysalesmanship.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<RefreshTokenResponse>

    @PUT("auth/password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<Unit>

    @GET("pull")
    suspend fun pullData(): Response<PullResponse>

    @POST("upload/visit")
    suspend fun uploadVisit(
        @Body payload: UploadPayload
    ): Response<Unit>
}
