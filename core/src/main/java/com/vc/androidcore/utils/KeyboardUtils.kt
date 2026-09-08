package com.vc.androidcore.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

/**
 * Modern keyboard utilities using [WindowInsetsControllerCompat].
 */
object KeyboardUtils {

    fun show(activity: Activity, view: View) {
        view.requestFocus()
        val controller = WindowInsetsControllerCompat(activity.window, view)
        controller.show(WindowInsetsCompat.Type.ime())
    }

    fun hide(activity: Activity) {
        val currentFocus = activity.currentFocus ?: activity.window.decorView
        val controller = WindowInsetsControllerCompat(activity.window, currentFocus)
        controller.hide(WindowInsetsCompat.Type.ime())
    }

    fun hide(view: View) {
        val context = view.context
        if (context is Activity) {
            val controller = WindowInsetsControllerCompat(context.window, view)
            controller.hide(WindowInsetsCompat.Type.ime())
        } else {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun clearFocusAndHide(view: View) {
        view.clearFocus()
        hide(view)
    }
}

fun Activity.hideKeyboard() = KeyboardUtils.hide(this)
fun Activity.showKeyboard(view: View) = KeyboardUtils.show(this, view)
fun Fragment.hideKeyboard() = activity?.let { KeyboardUtils.hide(it) }
fun Fragment.showKeyboard(view: View) = activity?.let { KeyboardUtils.show(it, view) }
fun View.hideKeyboard() = KeyboardUtils.hide(this)
fun View.clearFocusAndHideKeyboard() = KeyboardUtils.clearFocusAndHide(this)
