package com.vc.composecore.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Safely collects one-time [UiEffect] flows within a Compose lifecycle without emitting duplicate
 * effects across recompositions or configuration changes.
 *
 * @param flow The effect [Flow] usually exposed by a [BaseComposeViewModel].
 * @param minActiveState The minimum lifecycle state required to collect effects (defaults to [Lifecycle.State.STARTED]).
 * @param onEffect Lambda callback executed for each incoming effect.
 */
@Composable
fun <EFFECT : UiEffect> CollectEffect(
    flow: Flow<EFFECT>,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    onEffect: suspend (EFFECT) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            flow.collect { effect ->
                onEffect(effect)
            }
        }
    }
}
