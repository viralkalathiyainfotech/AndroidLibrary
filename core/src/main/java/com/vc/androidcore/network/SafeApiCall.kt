package com.vc.androidcore.network

import com.vc.androidcore.error.ErrorMapper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

/**
 * Safely executes a Retrofit suspend API call and returns a strongly-typed [NetworkResult].
 * Handles HTTP error codes, connectivity failures, timeouts, and JSON serialization bugs.
 * Supports configurable automated retries with exponential backoff for transient server/network failures.
 *
 * @param dispatcher CoroutineDispatcher on which to execute the network call (default: Dispatchers.IO).
 * @param retryCount Number of retry attempts on transient network or 5xx server errors (default: 0).
 * @param retryDelayMs Initial delay before the first retry attempt in milliseconds (default: 1000ms).
 * @param apiCall Lambda executing the Retrofit [Response] call.
 */
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> Response<T>
): NetworkResult<T> = safeApiCall(
    dispatcher = dispatcher,
    retryCount = 0,
    retryDelayMs = 1000L,
    apiCall = apiCall
)

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    retryCount: Int = 0,
    retryDelayMs: Long = 1000L,
    apiCall: suspend () -> Response<T>
): NetworkResult<T> = withContext(dispatcher) {
    var currentAttempt = 0
    var currentDelay = retryDelayMs

    while (true) {
        val result: NetworkResult<T>? = try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    NetworkResult.Success(body)
                } else if (response.code() == 204 || response.code() == 205) {
                    @Suppress("UNCHECKED_CAST")
                    NetworkResult.Success(Unit as T)
                } else {
                    NetworkResult.Error(
                        code = response.code(),
                        message = "Response body was empty."
                    )
                }
            } else {
                val errorCode = response.code()
                if (errorCode in 500..599 && currentAttempt < retryCount) {
                    null
                } else {
                    val errorBody = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        null
                    }
                    val errorMessage = errorBody?.ifBlank { null }
                        ?: response.message().ifBlank { null }
                        ?: "HTTP $errorCode error occurred."

                    NetworkResult.Error(
                        code = errorCode,
                        message = errorMessage,
                        appError = ErrorMapper.mapHttpCode(errorCode, errorMessage)
                    )
                }
            }
        } catch (throwable: Throwable) {
            if (throwable is CancellationException && throwable !is TimeoutCancellationException) {
                throw throwable
            }

            if (currentAttempt < retryCount && (throwable is IOException || throwable is TimeoutCancellationException)) {
                null
            } else {
                val appError = ErrorMapper.map(throwable)
                NetworkResult.Error(
                    code = null,
                    message = appError.userMessage,
                    throwable = throwable,
                    appError = appError
                )
            }
        }

        if (result != null) {
            return@withContext result
        }

        currentAttempt++
        delay(currentDelay)
        currentDelay *= 2
    }
    @Suppress("UNREACHABLE_CODE")
    error("Unexpected loop termination")
}
