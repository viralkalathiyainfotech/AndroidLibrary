package com.vc.androidcore.error

/**
 * Domain-level error hierarchy representing common application and network failures.
 */
sealed class AppError {

    abstract val userMessage: String

    data object Network : AppError() {
        override val userMessage: String = "No internet connection. Please check your network and try again."
    }

    data object Timeout : AppError() {
        override val userMessage: String = "The connection timed out. Please try again."
    }

    data object Unauthorized : AppError() {
        override val userMessage: String = "Session expired or unauthorized. Please log in again."
    }

    data object Forbidden : AppError() {
        override val userMessage: String = "Access denied. You do not have permission to view this resource."
    }

    data object NotFound : AppError() {
        override val userMessage: String = "The requested resource was not found."
    }

    data class Server(
        val code: Int,
        val message: String? = null
    ) : AppError() {
        override val userMessage: String = message ?: "Server error occurred ($code). Please try again later."
    }

    data class Validation(
        val message: String? = null
    ) : AppError() {
        override val userMessage: String = message ?: "Invalid input. Please check your data."
    }

    data class Unknown(
        val message: String? = null,
        val throwable: Throwable? = null
    ) : AppError() {
        override val userMessage: String = message ?: "An unexpected error occurred. Please try again."
    }
}
