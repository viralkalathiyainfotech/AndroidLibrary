package com.vc.androidcore.network

import java.io.IOException

/**
 * Exception representing an HTTP error returned by an API server.
 */
class ApiException(
    val statusCode: Int,
    override val message: String,
    val errorBody: String? = null
) : IOException(message)

/**
 * Exception representing connectivity, timeout, or DNS resolution failure.
 */
class NetworkException(
    override val message: String = "No network connection available",
    override val cause: Throwable? = null
) : IOException(message, cause)
