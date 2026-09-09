package com.vc.composecore.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vc.androidcore.error.AppError
import com.vc.androidcore.state.UiState
import com.vc.composecore.components.loading.CoreLoadingOverlay
import com.vc.composecore.components.status.CoreEmptyView
import com.vc.composecore.components.status.CoreErrorView
import com.vc.composecore.components.status.CoreMaintenanceView
import com.vc.composecore.components.status.CoreNoInternetView
import com.vc.composecore.components.status.CoreUnauthorizedView
import com.vc.composecore.resources.CoreText
import com.vc.composecore.state.ScreenState
import com.vc.composecore.state.toScreenState

/**
 * Generic state screen providing out-of-the-box handling for all presentation states
 * (Loading, Success, Empty, Error, NoInternet, Unauthorized, Maintenance) while giving
 * consumers the ability to customize or completely override any state slot.
 */
@Composable
fun <T> CoreStateScreen(
    state: ScreenState<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onLoginClick: (() -> Unit)? = null,
    loadingContent: @Composable () -> Unit = { CoreLoadingOverlay() },
    emptyContent: @Composable (ScreenState.Empty) -> Unit = { empty ->
        CoreEmptyView(
            title = empty.title?.asString() ?: "No Data Available",
            message = empty.message?.asString() ?: "There are no items to display at this time."
        )
    },
    errorContent: @Composable (ScreenState.Error) -> Unit = { error ->
        CoreErrorView(
            message = error.message.asString(),
            onRetry = if (error.retryable) onRetry else null
        )
    },
    noInternetContent: @Composable () -> Unit = {
        CoreNoInternetView(onRetry = onRetry)
    },
    unauthorizedContent: @Composable () -> Unit = {
        CoreUnauthorizedView(onLoginClick = onLoginClick)
    },
    maintenanceContent: @Composable () -> Unit = {
        CoreMaintenanceView()
    },
    content: @Composable (T) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (state) {
            is ScreenState.Idle -> {
                // Keep blank or idle placeholder
            }
            is ScreenState.Loading -> {
                loadingContent()
            }
            is ScreenState.Success -> {
                content(state.data)
            }
            is ScreenState.Empty -> {
                emptyContent(state)
            }
            is ScreenState.Error -> {
                errorContent(state)
            }
            is ScreenState.NoInternet -> {
                noInternetContent()
            }
            is ScreenState.Unauthorized -> {
                unauthorizedContent()
            }
            is ScreenState.Maintenance -> {
                maintenanceContent()
            }
        }
    }
}

/**
 * Overload for existing [UiState] instances from the core library.
 */
@Composable
fun <T> CoreStateScreen(
    state: UiState<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    emptyPredicate: ((T) -> Boolean)? = null,
    onLoginClick: (() -> Unit)? = null,
    loadingContent: @Composable () -> Unit = { CoreLoadingOverlay() },
    emptyContent: @Composable (ScreenState.Empty) -> Unit = { empty ->
        CoreEmptyView(
            title = empty.title?.asString() ?: "No Data Available",
            message = empty.message?.asString() ?: "There are no items to display at this time."
        )
    },
    errorContent: @Composable (ScreenState.Error) -> Unit = { error ->
        CoreErrorView(
            message = error.message.asString(),
            onRetry = onRetry
        )
    },
    noInternetContent: @Composable () -> Unit = { CoreNoInternetView(onRetry = onRetry) },
    unauthorizedContent: @Composable () -> Unit = { CoreUnauthorizedView(onLoginClick = onLoginClick) },
    content: @Composable (T) -> Unit
) {
    val screenState = state.toScreenState(emptyPredicate)
    CoreStateScreen(
        state = screenState,
        onRetry = onRetry,
        modifier = modifier,
        onLoginClick = onLoginClick,
        loadingContent = loadingContent,
        emptyContent = emptyContent,
        errorContent = errorContent,
        noInternetContent = noInternetContent,
        unauthorizedContent = unauthorizedContent,
        content = content
    )
}
