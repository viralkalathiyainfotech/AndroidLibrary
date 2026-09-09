package com.vc.composecore.state

import androidx.compose.runtime.Immutable
import com.vc.androidcore.error.AppError
import com.vc.androidcore.state.UiState
import com.vc.composecore.resources.CoreText

/**
 * Robust, production-grade presentation state covering all lifecycle and network states.
 */
@Immutable
sealed interface ScreenState<out T> : ViewState {

    data object Idle : ScreenState<Nothing>

    data class Loading(
        val message: CoreText? = null,
        val progress: Float? = null
    ) : ScreenState<Nothing>

    data class Success<out T>(
        val data: T
    ) : ScreenState<T>

    data class Empty(
        val title: CoreText? = null,
        val message: CoreText? = null,
        val actionText: CoreText? = null
    ) : ScreenState<Nothing>

    data class Error(
        val message: CoreText,
        val appError: AppError? = null,
        val retryable: Boolean = true
    ) : ScreenState<Nothing>

    data class NoInternet(
        val message: CoreText? = null
    ) : ScreenState<Nothing>

    data class Unauthorized(
        val message: CoreText? = null
    ) : ScreenState<Nothing>

    data class Maintenance(
        val message: CoreText? = null,
        val estimatedEndTime: String? = null
    ) : ScreenState<Nothing>

    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isEmpty: Boolean get() = this is Empty

    fun getDataOrNull(): T? = (this as? Success)?.data
}

/**
 * Converts existing core [UiState] into modern [ScreenState].
 */
fun <T> UiState<T>.toScreenState(emptyCheck: ((T) -> Boolean)? = null): ScreenState<T> {
    return when (this) {
        is UiState.Idle -> ScreenState.Idle
        is UiState.Loading -> ScreenState.Loading()
        is UiState.Success -> {
            if (emptyCheck != null && emptyCheck(data)) {
                ScreenState.Empty()
            } else {
                ScreenState.Success(data)
            }
        }
        is UiState.Error -> {
            when (this.appError) {
                AppError.Network -> ScreenState.NoInternet(CoreText.Dynamic(this.message))
                AppError.Unauthorized -> ScreenState.Unauthorized(CoreText.Dynamic(this.message))
                else -> ScreenState.Error(
                    message = CoreText.Dynamic(this.message),
                    appError = this.appError
                )
            }
        }
    }
}
