package com.vc.androidcore.utils

import android.os.SystemClock
import android.view.View
import androidx.core.view.isVisible

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.showIf(condition: Boolean) {
    this.visibility = if (condition) View.VISIBLE else View.GONE
}

fun View.hideIf(condition: Boolean) {
    this.visibility = if (condition) View.GONE else View.VISIBLE
}

/**
 * Prevents rapid multiple clicks by ignoring clicks within [debounceTimeMs].
 */
fun View.setOnDebouncedClickListener(debounceTimeMs: Long = 600L, onClick: (View) -> Unit) {
    var lastClickTime: Long = 0
    this.setOnClickListener { v ->
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime >= debounceTimeMs) {
            lastClickTime = currentTime
            onClick(v)
        }
    }
}
