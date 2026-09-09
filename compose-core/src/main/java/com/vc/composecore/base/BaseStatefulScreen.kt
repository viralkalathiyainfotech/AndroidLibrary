package com.vc.composecore.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vc.composecore.state.CollectEffect
import com.vc.composecore.state.UiAction
import com.vc.composecore.state.UiEffect
import com.vc.composecore.state.ViewState

/**
 * Stateful container connecting a [BaseComposeViewModel] to a Composable tree.
 * Automatically observes state with lifecycle awareness and safely processes side effects.
 */
@Composable
fun <STATE : ViewState, ACTION : UiAction, EFFECT : UiEffect> BaseStatefulScreen(
    viewModel: BaseComposeViewModel<STATE, ACTION, EFFECT>,
    onEffect: (suspend (EFFECT) -> Unit)? = null,
    content: @Composable (state: STATE, dispatch: (ACTION) -> Unit) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (onEffect != null) {
        CollectEffect(flow = viewModel.effect) { effect ->
            onEffect(effect)
        }
    }

    content(state, viewModel::dispatch)
}
