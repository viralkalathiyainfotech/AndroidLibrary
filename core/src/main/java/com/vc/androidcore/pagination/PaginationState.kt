package com.vc.androidcore.pagination

import com.vc.androidcore.error.AppError

/**
 * State representations for paginated feeds.
 */
sealed class PaginationState {
    data object Idle : PaginationState()
    data object Loading : PaginationState()
    data object Empty : PaginationState()
    data class Error(val message: String, val error: AppError? = null) : PaginationState()
    data object EndOfPagination : PaginationState()
}
