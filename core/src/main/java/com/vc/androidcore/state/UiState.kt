package com.vc.androidcore.state

import com.vc.androidcore.error.AppError
import com.vc.androidcore.error.ErrorMapper

/**
 * Standard generic UI state wrapper representing presentation states.
 */
sealed class UiState<out T> {

    data object Idle : UiState<Nothing>()

    data object Loading : UiState<Nothing>()

    data class Success<out T>(
        val data: T
    ) : UiState<T>()

    data class Error(
        val message: String,
        val throwable: Throwable? = null,
        val code: Int? = null,
        val appError: AppError = throwable?.let { ErrorMapper.map(it) } ?: ErrorMapper.mapHttpCode(code ?: 0, message)
    ) : UiState<Nothing>()

    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isIdle: Boolean get() = this is Idle

    fun getDataOrNull(): T? = (this as? Success)?.data

    inline fun onSuccess(block: (T) -> Unit): UiState<T> {
        if (this is Success) block(data)
        return this
    }

    inline fun onError(block: (message: String, throwable: Throwable?) -> Unit): UiState<T> {
        if (this is Error) block(message, throwable)
        return this
    }

    inline fun onLoading(block: () -> Unit): UiState<T> {
        if (this is Loading) block()
        return this
    }
}
