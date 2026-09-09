package com.vc.composecore.state

import androidx.compose.runtime.Immutable

/**
 * Marker interface for all immutable UI state models.
 * State must always be a data class or sealed class implementing [ViewState].
 */
@Immutable
interface ViewState
