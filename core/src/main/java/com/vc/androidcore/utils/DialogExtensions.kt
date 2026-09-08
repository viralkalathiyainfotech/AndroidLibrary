package com.vc.androidcore.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Displays a Material confirmation dialog with positive and negative action callbacks.
 *
 * Example:
 * ```kotlin
 * showConfirmDialog(
 *     title = "Delete Item",
 *     message = "Are you sure you want to delete this?",
 *     positiveText = "Delete",
 *     onPositive = { deleteItem() }
 * )
 * ```
 */
fun Context.showConfirmDialog(
    title: CharSequence? = null,
    message: CharSequence? = null,
    positiveText: CharSequence = "OK",
    negativeText: CharSequence = "Cancel",
    isCancelable: Boolean = true,
    onNegative: (() -> Unit)? = null,
    onPositive: () -> Unit
): AlertDialog {
    return MaterialAlertDialogBuilder(this)
        .apply {
            title?.let { setTitle(it) }
            message?.let { setMessage(it) }
            setPositiveButton(positiveText) { _, _ -> onPositive() }
            setNegativeButton(negativeText) { _, _ -> onNegative?.invoke() }
            setCancelable(isCancelable)
        }
        .show()
}

/**
 * Displays a simple Material Alert dialog with a single dismiss button.
 */
fun Context.showAlertDialog(
    title: CharSequence? = null,
    message: CharSequence? = null,
    buttonText: CharSequence = "OK",
    isCancelable: Boolean = true,
    onDismiss: (() -> Unit)? = null
): AlertDialog {
    return MaterialAlertDialogBuilder(this)
        .apply {
            title?.let { setTitle(it) }
            message?.let { setMessage(it) }
            setPositiveButton(buttonText) { _, _ -> onDismiss?.invoke() }
            setCancelable(isCancelable)
            setOnDismissListener { onDismiss?.invoke() }
        }
        .show()
}

/**
 * Displays a Material confirmation dialog from a [Fragment].
 */
fun Fragment.showConfirmDialog(
    title: CharSequence? = null,
    message: CharSequence? = null,
    positiveText: CharSequence = "OK",
    negativeText: CharSequence = "Cancel",
    isCancelable: Boolean = true,
    onNegative: (() -> Unit)? = null,
    onPositive: () -> Unit
): AlertDialog {
    return requireContext().showConfirmDialog(
        title = title,
        message = message,
        positiveText = positiveText,
        negativeText = negativeText,
        isCancelable = isCancelable,
        onNegative = onNegative,
        onPositive = onPositive
    )
}

/**
 * Displays a simple Material Alert dialog from a [Fragment].
 */
fun Fragment.showAlertDialog(
    title: CharSequence? = null,
    message: CharSequence? = null,
    buttonText: CharSequence = "OK",
    isCancelable: Boolean = true,
    onDismiss: (() -> Unit)? = null
): AlertDialog {
    return requireContext().showAlertDialog(
        title = title,
        message = message,
        buttonText = buttonText,
        isCancelable = isCancelable,
        onDismiss = onDismiss
    )
}
