package com.vc.composecore.error

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/**
 * Centralized error mapper converting any [Throwable] or HTTP response code into an [AppError].
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
            is NoRouteToHostException,
            is SSLException,
            is IOException -> AppError.Network

            is HttpException -> mapHttpException(throwable)

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
