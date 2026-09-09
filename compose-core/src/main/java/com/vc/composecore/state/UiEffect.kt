package com.vc.composecore.state

import androidx.compose.runtime.Immutable
import com.vc.composecore.resources.CoreText

/**
 * Marker interface for one-time side-effects emitted by ViewModels and consumed by the UI.
 * Unlike state, effects must only be processed once and must not survive recomposition.
 */
@Immutable
interface UiEffect {

    /**
     * Standard navigation effect.
     */
    data class Navigate(
        val route: String,
        val popUpToRoute: String? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = true,
        val restoreState: Boolean = true
    ) : UiEffect

    /**
     * Standard back-navigation effect.
     */
    data class NavigateBack(
        val resultKey: String? = null,
        val result: Any? = null
    ) : UiEffect

    /**
     * Standard snackbar display effect.
     */
    data class ShowSnackbar(
        val message: CoreText,
        val actionLabel: CoreText? = null,
        val onAction: (() -> Unit)? = null,
        val duration: SnackbarDuration = SnackbarDuration.Short,
        val type: SnackbarType = SnackbarType.Info
    ) : UiEffect

    /**
     * Standard toast display effect.
     */
    data class ShowToast(
        val message: CoreText,
        val isLong: Boolean = false
    ) : UiEffect

    /**
     * Bottom sheet trigger effect.
     */
    data class ShowBottomSheet(
        val sheetId: String,
        val payload: Any? = null
    ) : UiEffect

    /**
     * Dialog trigger effect.
     */
    data class ShowDialog(
        val dialogId: String,
        val payload: Any? = null
    ) : UiEffect

    enum class SnackbarDuration { Short, Long, Indefinite }
    enum class SnackbarType { Success, Error, Warning, Info }
}
