package com.vc.androidcore.utils

import android.widget.EditText
import android.widget.TextView

/**
 * Returns trimmed string text from an [EditText].
 */
fun EditText.textValue(): String = this.text?.toString()?.trim().orEmpty()

/**
 * Sets text only when string is not null and not blank, otherwise hides or ignores.
 */
fun TextView.setTextIfNotEmpty(text: String?, hideIfEmpty: Boolean = false) {
    if (!text.isNullOrBlank()) {
        this.text = text
        this.visible()
    } else {
        if (hideIfEmpty) this.gone()
    }
}
