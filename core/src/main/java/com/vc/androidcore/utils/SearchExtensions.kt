package com.vc.androidcore.utils

import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Debounces text changes on an [EditText] for search queries.
 * Cancels previous delay on new input and only triggers callback after user stops typing.
 *
 * @param debounceMs Milliseconds to delay before dispatching query (default: 400ms).
 * @param scope CoroutineScope in which the debounce timer runs (e.g. lifecycleScope / viewModelScope).
 * @param onQuery Lambda executed on the coroutine scope when user pauses typing.
 */
fun EditText.onSearchQuery(
    debounceMs: Long = 400L,
    scope: CoroutineScope,
    onQuery: (String) -> Unit
) {
    var searchJob: Job? = null
    doAfterTextChanged { editable ->
        val query = editable?.toString()?.trim().orEmpty()
        searchJob?.cancel()
        searchJob = scope.launch {
            delay(debounceMs)
            onQuery(query)
        }
    }
}
