package com.vc.androidcore.state

import com.vc.androidcore.error.AppError

/**
 * Encapsulates error presentation state with retry capability.
 */
data class ErrorState(
    val message: String,
    val error: AppError? = null,
    val canRetry: Boolean = true,
    val onRetry: (() -> Unit)? = null
)
