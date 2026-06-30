package com.pndnwngi.mysalesmanship.data.remote

import com.google.gson.Gson
import com.pndnwngi.mysalesmanship.BuildConfig
import com.pndnwngi.mysalesmanship.data.local.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefreshInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val gson: Gson
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)

        if (response.code == 401 && !request.url.encodedPath.contains("auth/login") && !request.url.encodedPath.contains("auth/refresh")) {
            synchronized(this) {
                // Get the current token again (it might have been refreshed by another thread)
                val currentAccessToken = runBlocking { tokenManager.accessToken.firstOrNull() }
                val authHeader = request.header("Authorization")
                
                // If the token in the request is already different from currentAccessToken,
                // it means another thread already refreshed it. We just retry with the new token.
                if (authHeader != null && authHeader != "Bearer $currentAccessToken") {
                    response.close()
                    val newRequest = request.newBuilder()
                        .header("Authorization", "Bearer $currentAccessToken")
                        .build()
                    return chain.proceed(newRequest)
                }

                // Otherwise, perform the refresh
                val refreshToken = runBlocking { tokenManager.refreshToken.firstOrNull() }
                if (!refreshToken.isNullOrEmpty()) {
                    val refreshedToken = refreshAccessToken(refreshToken)
                    if (refreshedToken != null) {
                        runBlocking {
                            tokenManager.saveTokens(refreshedToken, refreshToken)
                        }
                        response.close()
                        val newRequest = request.newBuilder()
                            .header("Authorization", "Bearer $refreshedToken")
                            .build()
                        return chain.proceed(newRequest)
                    } else {
                        // Refresh token is expired or invalid, clear tokens
                        runBlocking { tokenManager.clearTokens() }
                    }
                }
            }
        }

        return response
    }

    private fun refreshAccessToken(refreshToken: String): String? {
        val client = OkHttpClient.Builder().build() // Create a clean client without interceptors to avoid loops
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = gson.toJson(RefreshTokenRequest(refreshToken)).toRequestBody(mediaType)
        
        val baseUrl = BuildConfig.BASE_URL.removeSuffix("/")
        val url = "$baseUrl/auth/refresh"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val refreshResponse = gson.fromJson(responseBody, RefreshTokenResponse::class.java)
                refreshResponse.accessToken
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
