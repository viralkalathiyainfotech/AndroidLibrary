package com.vc.composecore.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vc.composecore.state.UiAction
import com.vc.composecore.state.ViewState

/**
 * Pure stateless screen presentation pattern. Takes immutable state and emits actions.
 */
@Composable
fun <STATE : ViewState, ACTION : UiAction> BaseStatelessScreen(
    state: STATE,
    onAction: (ACTION) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(state: STATE, dispatch: (ACTION) -> Unit) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content(state, onAction)
    }
}
