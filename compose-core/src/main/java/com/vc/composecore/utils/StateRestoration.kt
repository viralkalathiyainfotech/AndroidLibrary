package com.vc.composecore.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.StateFlow

/**
 * State restoration helpers preventing unnecessary UI resets during configuration changes.
 */

@Composable
fun rememberSaveableTextField(initialText: String = ""): MutableState<String> {
    return rememberSaveable { mutableStateOf(initialText) }
}

@Composable
fun rememberSaveableTab(initialTab: Int = 0): MutableState<Int> {
    return rememberSaveable { mutableIntStateOf(initialTab) }
}

@Composable
fun <T : Any> rememberSaveableFilter(initialFilter: T? = null): MutableState<T?> {
    return rememberSaveable { mutableStateOf(initialFilter) }
}

/**
 * Connects a [SavedStateHandle] directly to a Compose [MutableState].
 */
fun <T> SavedStateHandle.asSaveableState(key: String, initialValue: T): MutableState<T> {
    val state = mutableStateOf(this.get<T>(key) ?: initialValue)
    return object : MutableState<T> by state {
        override var value: T
            get() = state.value
            set(value) {
                state.value = value
                this@asSaveableState[key] = value
            }
    }
}
