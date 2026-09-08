package com.vc.androidcore.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.milliseconds

/**
 * Reactive debounce utilities powered by Kotlin Coroutines and Flows.
 */
object DebounceUtils {

    /**
     * Creates a text change [Flow] from an [EditText].
     */
    fun textChanges(editText: EditText): Flow<String> = callbackFlow {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                trySend(s?.toString()?.trim().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        editText.addTextChangedListener(watcher)
        awaitClose { editText.removeTextChangedListener(watcher) }
    }
}

/**
 * Extension on [EditText] to observe debounced search queries.
 */
@OptIn(FlowPreview::class)
fun EditText.onDebouncedQueryChange(
    scope: CoroutineScope,
    waitMs: Long = 400L,
    onQuery: (String) -> Unit
) {
    DebounceUtils.textChanges(this)
        .debounce(waitMs.milliseconds)
        .distinctUntilChanged()
        .onEach { onQuery(it) }
        .launchIn(scope)
}
