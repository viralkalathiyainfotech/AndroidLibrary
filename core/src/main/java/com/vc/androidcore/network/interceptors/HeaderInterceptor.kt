package com.vc.androidcore.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds default and custom headers to all outgoing requests.
 */
class HeaderInterceptor(
    private val defaultHeaders: Map<String, String> = emptyMap()
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Standard headers
        requestBuilder.header("Accept", "application/json")
        requestBuilder.header("Content-Type", "application/json")

        // Custom headers provided by the consuming app
        for ((name, value) in defaultHeaders) {
            requestBuilder.header(name, value)
        }

        return chain.proceed(requestBuilder.build())
    }
}
