package com.vc.composecore.utils

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Standard presentation-layer representation of user session lifecycle.
 */
@Immutable
sealed interface SessionState<out T> {
    data class Authenticated<out T>(val user: T) : SessionState<T>
    data object Unauthenticated : SessionState<Nothing>
    data object SessionExpired : SessionState<Nothing>
    data object RefreshingSession : SessionState<Nothing>

    val isAuthenticated: Boolean get() = this is Authenticated
}

/**
 * Generic session manager for holding and broadcasting active session state.
 */
class CoreSessionManager<T>(initialState: SessionState<T> = SessionState.Unauthenticated) {
    private val _sessionState = MutableStateFlow(initialState)
    val sessionState: StateFlow<SessionState<T>> = _sessionState.asStateFlow()

    fun setAuthenticated(user: T) {
        _sessionState.value = SessionState.Authenticated(user)
    }

    fun setUnauthenticated() {
        _sessionState.value = SessionState.Unauthenticated
    }

    fun setSessionExpired() {
        _sessionState.value = SessionState.SessionExpired
    }

    fun setRefreshing() {
        _sessionState.value = SessionState.RefreshingSession
    }
}
