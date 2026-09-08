package com.vc.androidcore.state

/**
 * Encapsulates dedicated loading states with optional user-facing message.
 */
sealed class LoadingState {
    data object Idle : LoadingState()
    data class Loading(val message: String? = null) : LoadingState()

    val isLoading: Boolean get() = this is Loading
}
