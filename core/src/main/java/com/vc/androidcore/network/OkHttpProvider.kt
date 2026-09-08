package com.vc.androidcore.network

import com.vc.androidcore.config.CoreLibrary
import com.vc.androidcore.network.interceptors.AuthInterceptor
import com.vc.androidcore.network.interceptors.HeaderInterceptor
import com.vc.androidcore.network.interceptors.LoggingInterceptor
import okhttp3.Cache
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Factory and builder for creating configured [OkHttpClient] instances.
 */
object OkHttpProvider {

    /**
     * Builder for creating custom [OkHttpClient] instances with flexible configuration.
     */
    class Builder {
        private var connectTimeoutSeconds: Long = CoreLibrary.config.defaultTimeout
        private var readTimeoutSeconds: Long = CoreLibrary.config.defaultTimeout
        private var writeTimeoutSeconds: Long = CoreLibrary.config.defaultTimeout
        private var retryOnConnectionFailure: Boolean = true
        private val interceptors: MutableList<Interceptor> = mutableListOf()
        private val networkInterceptors: MutableList<Interceptor> = mutableListOf()
        private var tokenProvider: TokenProvider? = null
        private var refreshTokenProvider: RefreshTokenProvider? = null
        private var customHeaders: Map<String, String> = emptyMap()
        private var enableLogging: Boolean = CoreLibrary.config.enableNetworkLogging
        private var cache: Cache? = null
        private var certificatePinner: CertificatePinner? = null

        fun connectTimeout(seconds: Long) = apply { this.connectTimeoutSeconds = seconds }
        fun readTimeout(seconds: Long) = apply { this.readTimeoutSeconds = seconds }
        fun writeTimeout(seconds: Long) = apply { this.writeTimeoutSeconds = seconds }
        fun retryOnConnectionFailure(retry: Boolean) = apply { this.retryOnConnectionFailure = retry }

        fun addInterceptor(interceptor: Interceptor) = apply { this.interceptors.add(interceptor) }
        fun addNetworkInterceptor(interceptor: Interceptor) = apply { this.networkInterceptors.add(interceptor) }

        fun tokenProvider(provider: TokenProvider, refreshProvider: RefreshTokenProvider? = null) = apply {
            this.tokenProvider = provider
            this.refreshTokenProvider = refreshProvider
        }

        fun headers(headers: Map<String, String>) = apply { this.customHeaders = headers }
        fun logging(enable: Boolean) = apply { this.enableLogging = enable }

        fun cache(directory: File, maxSize: Long = 10L * 1024L * 1024L) = apply {
            this.cache = Cache(directory, maxSize)
        }

        fun certificatePinner(pinner: CertificatePinner) = apply { this.certificatePinner = pinner }

        fun build(): OkHttpClient {
            val builder = OkHttpClient.Builder()
                .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(writeTimeoutSeconds, TimeUnit.SECONDS)
                .retryOnConnectionFailure(retryOnConnectionFailure)

            // Header Interceptor
            builder.addInterceptor(HeaderInterceptor(customHeaders))

            // Auth Interceptor if token provider is configured
            tokenProvider?.let { provider ->
                builder.addInterceptor(AuthInterceptor(provider, refreshTokenProvider))
            }

            // Custom app interceptors
            interceptors.forEach { builder.addInterceptor(it) }

            // Network interceptors
            networkInterceptors.forEach { builder.addNetworkInterceptor(it) }

            // Logging Interceptor (redacting sensitive data)
            if (enableLogging) {
                builder.addInterceptor(LoggingInterceptor())
            }

            cache?.let { builder.cache(it) }
            certificatePinner?.let { builder.certificatePinner(it) }

            return builder.build()
        }
    }

    fun builder(): Builder = Builder()

    /**
     * Creates a default [OkHttpClient] using global core configuration.
     */
    fun createDefault(tokenProvider: TokenProvider? = null): OkHttpClient {
        val builder = builder()
        if (tokenProvider != null) {
            builder.tokenProvider(tokenProvider)
        }
        return builder.build()
    }
}
