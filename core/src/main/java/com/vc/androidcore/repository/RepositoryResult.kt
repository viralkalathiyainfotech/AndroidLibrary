package com.vc.androidcore.repository

import com.vc.androidcore.error.AppError

/**
 * Result wrapper for repository operations supporting offline-first data caching.
 */
sealed class RepositoryResult<out T> {

    data class Success<out T>(
        val data: T,
        val isFromCache: Boolean = false
    ) : RepositoryResult<T>()

    data class Error(
        val message: String,
        val error: AppError? = null,
        val cachedData: Any? = null
    ) : RepositoryResult<Nothing>()

    data class Loading<out T>(
        val cachedData: T? = null
    ) : RepositoryResult<T>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getDataOrNull(): T? = when (this) {
        is Success -> data
        is Loading -> cachedData
        is Error -> cachedData as? T
    }
}
