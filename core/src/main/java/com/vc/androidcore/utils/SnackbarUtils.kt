package com.vc.androidcore.utils

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

/**
 * Snackbar utility extensions and helper methods.
 */
object SnackbarUtils {

    fun show(
        view: View,
        message: String,
        duration: Int = Snackbar.LENGTH_SHORT,
        actionText: String? = null,
        action: (() -> Unit)? = null
    ): Snackbar {
        val snackbar = Snackbar.make(view, message, duration)
        if (actionText != null && action != null) {
            snackbar.setAction(actionText) { action() }
        }
        snackbar.show()
        return snackbar
    }
}

fun View.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: String? = null,
    action: (() -> Unit)? = null
): Snackbar = SnackbarUtils.show(this, message, duration, actionText, action)

fun View.showSnackbar(
    @StringRes messageRes: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
    @StringRes actionRes: Int? = null,
    action: (() -> Unit)? = null
): Snackbar {
    val message = context.getString(messageRes)
    val actionText = actionRes?.let { context.getString(it) }
    return SnackbarUtils.show(this, message, duration, actionText, action)
}
