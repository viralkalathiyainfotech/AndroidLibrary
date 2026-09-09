package com.vc.composecore

import androidx.compose.ui.unit.dp
import com.vc.androidcore.error.AppError
import com.vc.androidcore.state.UiState
import com.vc.composecore.resources.asCoreText
import com.vc.composecore.state.ScreenState
import com.vc.composecore.state.toScreenState
import com.vc.composecore.theme.CoreAnimationDuration
import com.vc.composecore.theme.CoreBorderWidth
import com.vc.composecore.theme.CoreComponentHeight
import com.vc.composecore.theme.CoreElevation
import com.vc.composecore.theme.CoreIconSize
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreSpacing
import com.vc.composecore.utils.CoreSessionManager
import com.vc.composecore.utils.SessionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CoreTokensTest {

    @Test
    fun `verify design tokens values hierarchy`() {
        assertTrue(CoreSpacing.sm < CoreSpacing.md)
        assertTrue(CoreSpacing.md < CoreSpacing.lg)

        assertTrue(CoreRadius.small < CoreRadius.medium)
        assertTrue(CoreRadius.medium < CoreRadius.large)

        assertTrue(CoreElevation.level1 < CoreElevation.level2)
        assertTrue(CoreElevation.level2 < CoreElevation.level3)

        assertTrue(CoreBorderWidth.thin < CoreBorderWidth.medium)
        assertTrue(CoreIconSize.sm < CoreIconSize.md)

        assertTrue(CoreAnimationDuration.fast < CoreAnimationDuration.normal)
        assertTrue(CoreAnimationDuration.normal < CoreAnimationDuration.slow)
    }

    @Test
    fun `session state manager transitions accurately`() {
        val manager = CoreSessionManager<String>()

        assertEquals(SessionState.Unauthenticated, manager.sessionState.value)

        manager.setAuthenticated("user123")
        assertTrue(manager.sessionState.value is SessionState.Authenticated)
        assertEquals("user123", (manager.sessionState.value as SessionState.Authenticated).user)

        manager.setSessionExpired()
        assertEquals(SessionState.SessionExpired, manager.sessionState.value)

        manager.setRefreshing()
        assertEquals(SessionState.RefreshingSession, manager.sessionState.value)
    }

    @Test
    fun `screen state conversion from core UiState`() {
        val successUi: UiState<String> = UiState.Success("Hello")
        val screenState = successUi.toScreenState()
        assertTrue(screenState is ScreenState.Success)
        assertEquals("Hello", (screenState as ScreenState.Success).data)

        val emptyUi: UiState<List<String>> = UiState.Success(emptyList())
        val emptyScreenState = emptyUi.toScreenState { it.isEmpty() }
        assertTrue(emptyScreenState is ScreenState.Empty)

        val errorUi: UiState<String> = UiState.Error("Network Failed", appError = AppError.Network)
        val noInternetScreenState = errorUi.toScreenState()
        assertTrue(noInternetScreenState is ScreenState.NoInternet)
    }
}
