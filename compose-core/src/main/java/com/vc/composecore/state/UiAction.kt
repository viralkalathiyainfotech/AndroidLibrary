package com.vc.composecore.state

import androidx.compose.runtime.Immutable

/**
 * Marker interface for all user intentions/actions dispatched from the UI to the ViewModel.
 *
 * Example:
 * ```kotlin
 * sealed interface LoginAction : UiAction {
 *     data class EnterEmail(val email: String) : LoginAction
 *     data class EnterPassword(val password: String) : LoginAction
 *     data object Submit : LoginAction
 * }
 * ```
 */
@Immutable
interface UiAction
