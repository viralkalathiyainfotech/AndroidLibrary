package com.vc.androidcore.network

import com.vc.androidcore.error.AppError
import com.vc.androidcore.error.ErrorMapper

/**
 * Result wrapper for network operations, isolating API models from consumer business logic.
 */
sealed class NetworkResult<out T> {

    data class Success<out T>(
        val data: T
    ) : NetworkResult<T>()

    data class Error(
        val code: Int? = null,
        val message: String,
        val throwable: Throwable? = null,
        val appError: AppError = throwable?.let { ErrorMapper.map(it) } ?: ErrorMapper.mapHttpCode(code ?: 0, message)
    ) : NetworkResult<Nothing>()

    data class Loading(
        val isProgress: Boolean = true
    ) : NetworkResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = (this as? Success)?.data

    inline fun <R> map(transform: (T) -> R): NetworkResult<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(code, message, throwable, appError)
            is Loading -> Loading(isProgress)
        }
    }

    inline fun onSuccess(block: (T) -> Unit): NetworkResult<T> {
        if (this is Success) block(data)
        return this
    }

    inline fun onError(block: (code: Int?, message: String, throwable: Throwable?) -> Unit): NetworkResult<T> {
        if (this is Error) block(code, message, throwable)
        return this
    }
}
