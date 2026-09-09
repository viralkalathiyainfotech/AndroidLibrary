package com.vc.composecore.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

/**
 * Executes callbacks tied to Android Lifecycle events safely.
 */
@Composable
fun LifecycleAwareEffect(
    event: Lifecycle.Event,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: () -> Unit
) {
    val currentOnEvent = rememberUpdatedState(onEvent)
    DisposableEffect(lifecycleOwner, event) {
        val observer = LifecycleEventObserver { _, triggeredEvent ->
            if (triggeredEvent == event) {
                currentOnEvent.value()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun OnResume(onAction: () -> Unit) {
    LifecycleAwareEffect(event = Lifecycle.Event.ON_RESUME, onEvent = onAction)
}

@Composable
fun OnPause(onAction: () -> Unit) {
    LifecycleAwareEffect(event = Lifecycle.Event.ON_PAUSE, onEvent = onAction)
}

@Composable
fun OnStart(onAction: () -> Unit) {
    LifecycleAwareEffect(event = Lifecycle.Event.ON_START, onEvent = onAction)
}

@Composable
fun OnStop(onAction: () -> Unit) {
    LifecycleAwareEffect(event = Lifecycle.Event.ON_STOP, onEvent = onAction)
}

/**
 * Returns a reactive [State<Boolean>] indicating whether the application is in the foreground.
 */
@Composable
fun rememberIsAppInForeground(): State<Boolean> {
    val isForeground = remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> isForeground.value = true
                Lifecycle.Event.ON_STOP -> isForeground.value = false
                else -> {}
            }
        }
        val processLifecycle = ProcessLifecycleOwner.get().lifecycle
        processLifecycle.addObserver(observer)
        onDispose {
            processLifecycle.removeObserver(observer)
        }
    }

    return isForeground
}
