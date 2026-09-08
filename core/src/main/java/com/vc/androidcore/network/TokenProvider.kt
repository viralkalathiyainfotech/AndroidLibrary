package com.vc.androidcore.network

/**
 * Abstraction for providing authentication tokens to HTTP interceptors.
 * Applications implement this interface to provide access tokens from DataStore,
 * SharedPreferences, or memory without tight coupling.
 */
interface TokenProvider {
    /**
     * Returns the current access token, or null if unauthenticated.
     */
    fun getAccessToken(): String?
}

/**
 * Abstraction for handling token refresh cycles when an HTTP 401 Unauthorized is encountered.
 */
interface RefreshTokenProvider {
    /**
     * Asynchronously refreshes the access token using a refresh token.
     * Returns the new access token, or null if refresh failed.
     */
    suspend fun refreshToken(): String?

    /**
     * Callback triggered when authentication cannot be recovered and the user must re-authenticate.
     */
    fun onTokenExpired()
}
