package com.vc.androidcore.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.vc.androidcore.config.CoreLibrary
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Factory and builder for creating configured [Retrofit] instances.
 */
object RetrofitProvider {

    class Builder {
        private var baseUrl: String = CoreLibrary.config.baseUrl
        private var okHttpClient: OkHttpClient? = null
        private var gson: Gson = GsonBuilder().setLenient().create()
        private val converterFactories: MutableList<Converter.Factory> = mutableListOf()

        fun baseUrl(url: String) = apply {
            this.baseUrl = if (url.endsWith("/")) url else "$url/"
        }

        fun client(client: OkHttpClient) = apply { this.okHttpClient = client }

        fun gson(gson: Gson) = apply { this.gson = gson }

        fun addConverterFactory(factory: Converter.Factory) = apply {
            this.converterFactories.add(factory)
        }

        fun build(): Retrofit {
            require(baseUrl.isNotBlank()) { "Base URL must not be blank when building Retrofit." }

            val client = okHttpClient ?: OkHttpProvider.createDefault()

            val builder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)

            // Add custom converter factories first
            converterFactories.forEach { builder.addConverterFactory(it) }

            // Default to GsonConverterFactory
            if (converterFactories.isEmpty()) {
                builder.addConverterFactory(GsonConverterFactory.create(gson))
            }

            return builder.build()
        }
    }

    fun builder(): Builder = Builder()

    /**
     * Helper to create an API Service instance directly.
     */
    inline fun <reified T> createService(
        baseUrl: String,
        okHttpClient: OkHttpClient? = null,
        gson: Gson? = null
    ): T {
        val builder = builder().baseUrl(baseUrl)
        okHttpClient?.let { builder.client(it) }
        gson?.let { builder.gson(it) }
        return builder.build().create(T::class.java)
    }
}
