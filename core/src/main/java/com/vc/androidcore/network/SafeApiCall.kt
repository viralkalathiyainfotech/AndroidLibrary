package com.vc.androidcore.network

import com.vc.androidcore.error.ErrorMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Safely executes a Retrofit suspend API call and returns a strongly-typed [NetworkResult].
 * Handles HTTP error codes, connectivity failures, timeouts, and JSON serialization bugs.
 *
 * @param dispatcher CoroutineDispatcher on which to execute the network call (default: Dispatchers.IO).
 * @param apiCall Lambda executing the Retrofit [Response] call.
 */
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> Response<T>
): NetworkResult<T> {
    return withContext(dispatcher) {
        try {
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
        } catch (throwable: Throwable) {
            val appError = ErrorMapper.map(throwable)
            NetworkResult.Error(
                code = null,
                message = appError.userMessage,
                throwable = throwable,
                appError = appError
            )
        }
    }
}
