package com.vc.androidcore.config

import android.content.Context

/**
 * Global configuration options for the AndroidCoreLibrary.
 *
 * @property enableLogging Controls general library logging.
 * @property enableNetworkLogging Controls HTTP request/response logging in OkHttp.
 * @property defaultTimeout Default timeout in seconds for network calls (connect, read, write).
 * @property baseUrl Optional default base URL for Retrofit API calls.
 */
data class CoreConfig(
    val enableLogging: Boolean = true,
    val enableNetworkLogging: Boolean = false,
    val defaultTimeout: Long = 30L,
    val baseUrl: String = ""
)

/**
 * Initialization and configuration entry point for the AndroidCoreLibrary.
 */
object CoreLibrary {

    @Volatile
    private var _config: CoreConfig = CoreConfig()
    val config: CoreConfig get() = _config

    private var _appContext: Context? = null
    val appContext: Context
        get() = _appContext ?: throw IllegalStateException(
            "CoreLibrary is not initialized. Please call CoreLibrary.initialize(context, config) in Application.onCreate()."
        )

    /**
     * Initializes the core library with the application context and optional configuration.
     *
     * @param context Application context.
     * @param config Custom [CoreConfig] settings.
     */
    fun initialize(context: Context, config: CoreConfig = CoreConfig()) {
        _appContext = context.applicationContext
        _config = config
    }

    /**
     * Updates the library configuration at runtime.
     */
    fun updateConfig(config: CoreConfig) {
        _config = config
    }
}
