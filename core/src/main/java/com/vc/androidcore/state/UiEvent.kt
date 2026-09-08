package com.vc.androidcore.state

import android.os.Bundle
import com.vc.androidcore.error.AppError

/**
 * Single-shot, one-time UI events consumed via SharedFlow in Activities and Fragments.
 */
sealed class UiEvent {

    data class ShowToast(
        val message: String,
        val isLong: Boolean = false
    ) : UiEvent()

    data class ShowSnackbar(
        val message: String,
        val actionText: String? = null,
        val action: (() -> Unit)? = null
    ) : UiEvent()

    data class Navigate(
        val destination: Class<*>? = null,
        val extras: Bundle? = null,
        val finishCurrent: Boolean = false
    ) : UiEvent()

    data class ShowDialog(
        val title: String,
        val message: String
    ) : UiEvent()

    data class ApiError(
        val error: AppError
    ) : UiEvent()

    data object Logout : UiEvent()

    data class Custom(
        val payload: Any?
    ) : UiEvent()
}
