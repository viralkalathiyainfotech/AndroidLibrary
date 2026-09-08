package com.vc.androidcore.network.interceptors

import com.vc.androidcore.network.RefreshTokenProvider
import com.vc.androidcore.network.TokenProvider
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor for adding Bearer authentication token and handling token refresh cycles.
 */
class AuthInterceptor(
    private val tokenProvider: TokenProvider,
    private val refreshTokenProvider: RefreshTokenProvider? = null
) : Interceptor {

    companion object {
        const val HEADER_NO_AUTH = "No-Authentication"
        const val HEADER_AUTHORIZATION = "Authorization"
        private val refreshMutex = Mutex()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Check if authentication should be skipped for this endpoint
        if (originalRequest.header(HEADER_NO_AUTH) != null) {
            val cleanRequest = originalRequest.newBuilder()
                .removeHeader(HEADER_NO_AUTH)
                .build()
            return chain.proceed(cleanRequest)
        }

        val token = tokenProvider.getAccessToken()
        val requestWithToken = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header(HEADER_AUTHORIZATION, "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(requestWithToken)

        // Handle 401 Unauthorized with token refresh if provider is supplied
        if (response.code == 401 && refreshTokenProvider != null) {
            response.close() // Must close response body before retrying

            val newToken = runBlocking {
                refreshMutex.withLock {
                    // Re-check token in case another thread already refreshed it
                    val currentToken = tokenProvider.getAccessToken()
                    if (currentToken != null && currentToken != token) {
                        currentToken
                    } else {
                        refreshTokenProvider.refreshToken()
                    }
                }
            }

            return if (!newToken.isNullOrBlank()) {
                val newRequest = originalRequest.newBuilder()
                    .header(HEADER_AUTHORIZATION, "Bearer $newToken")
                    .build()
                chain.proceed(newRequest)
            } else {
                refreshTokenProvider.onTokenExpired()
                // Return an unauthorized response if token could not be refreshed
                chain.proceed(requestWithToken)
            }
        }

        return response
    }
}
