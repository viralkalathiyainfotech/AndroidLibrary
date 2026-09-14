package com.vc.composecore.logging

import android.util.Log

/**
 * Production-ready logger for the Compose library and client applications.
 * Automatically respects logging settings and scrubs sensitive details.
 */
object CoreLogger {

    var enableLogging: Boolean = true

    private const val DEFAULT_TAG = "ComposeCore"

    // Sensitive keyword patterns to scrub from log messages
    private val SENSITIVE_PATTERNS = listOf(
        Regex("(?i)(password|passwd|secret|token|bearer|authorization|api[_-]?key)\\s*[:=]\\s*([^\",\\s]+)"),
        Regex("(?i)(\"password\"\\s*:\\s*\")([^\"]+)(\")"),
        Regex("(?i)(\"token\"\\s*:\\s*\")([^\"]+)(\")"),
        Regex("(?i)(\"access_token\"\\s*:\\s*\")([^\"]+)(\")")
    )

    private fun scrubSensitiveData(message: String): String {
        var sanitized = message
        for (pattern in SENSITIVE_PATTERNS) {
            sanitized = pattern.replace(sanitized) { matchResult ->
                val prefix = matchResult.groupValues[1]
                "$prefix: [REDACTED]"
            }
        }
        return sanitized
    }

    private fun isLoggable(): Boolean = enableLogging

    /**
     * Log DEBUG message.
     */
    fun d(message: String, tag: String = DEFAULT_TAG, throwable: Throwable? = null) {
        if (!isLoggable()) return
        val safeMessage = scrubSensitiveData(message)
        runCatching {
            if (throwable != null) {
                Log.d(tag, safeMessage, throwable)
            } else {
                Log.d(tag, safeMessage)
            }
        }
    }

    /**
     * Log INFO message.
     */
    fun i(message: String, tag: String = DEFAULT_TAG, throwable: Throwable? = null) {
        if (!isLoggable()) return
        val safeMessage = scrubSensitiveData(message)
        runCatching {
            if (throwable != null) {
                Log.i(tag, safeMessage, throwable)
            } else {
                Log.i(tag, safeMessage)
            }
        }
    }

    /**
     * Log WARNING message.
     */
    fun w(message: String, tag: String = DEFAULT_TAG, throwable: Throwable? = null) {
        if (!isLoggable()) return
        val safeMessage = scrubSensitiveData(message)
        runCatching {
            if (throwable != null) {
                Log.w(tag, safeMessage, throwable)
            } else {
                Log.w(tag, safeMessage)
            }
        }
    }

    /**
     * Log ERROR message.
     */
    fun e(message: String, tag: String = DEFAULT_TAG, throwable: Throwable? = null) {
        if (!isLoggable()) return
        val safeMessage = scrubSensitiveData(message)
        runCatching {
            if (throwable != null) {
                Log.e(tag, safeMessage, throwable)
            } else {
                Log.e(tag, safeMessage)
            }
        }
    }
}
