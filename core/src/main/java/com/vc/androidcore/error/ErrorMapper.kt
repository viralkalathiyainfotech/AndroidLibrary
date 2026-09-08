package com.vc.androidcore.error

import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/**
 * Centralized error mapper converting any [Throwable] into a structured [AppError].
 */
object ErrorMapper {

    /**
     * Maps a given [Throwable] to an [AppError].
     */
    fun map(throwable: Throwable): AppError {
        return when (throwable) {
            is SocketTimeoutException -> AppError.Timeout

            is UnknownHostException,
            is ConnectException,
            is NoRouteToHostException -> AppError.Network

            is SSLException -> AppError.Network

            is HttpException -> mapHttpException(throwable)

            is JsonParseException,
            is MalformedJsonException -> AppError.Unknown("Failed to parse response from server", throwable)

            is IOException -> AppError.Network

            else -> AppError.Unknown(throwable.localizedMessage ?: "Unknown error occurred", throwable)
        }
    }

    /**
     * Maps HTTP status codes to corresponding [AppError] representations.
     */
    fun mapHttpCode(code: Int, message: String? = null): AppError {
        return when (code) {
            400, 422 -> AppError.Validation(message)
            401 -> AppError.Unauthorized
            403 -> AppError.Forbidden
            404 -> AppError.NotFound
            408 -> AppError.Timeout
            in 500..599 -> AppError.Server(code, message)
            else -> AppError.Server(code, message)
        }
    }

    private fun mapHttpException(httpException: HttpException): AppError {
        val code = httpException.code()
        val errorBody = try {
            httpException.response()?.errorBody()?.string()
        } catch (e: Exception) {
            null
        }
        val message = errorBody ?: httpException.message()
        return mapHttpCode(code, message)
    }
}
