package com.vc.androidcore.utils

import android.content.Context
import android.widget.Toast
import java.lang.ref.WeakReference

/**
 * Toast helper that prevents overlapping duplicate toasts.
 */
object ToastUtils {

    private var currentToast: WeakReference<Toast>? = null

    fun show(context: Context, message: String, isLong: Boolean = false) {
        currentToast?.get()?.cancel()
        val duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        val toast = Toast.makeText(context.applicationContext, message, duration)
        currentToast = WeakReference(toast)
        toast.show()
    }
}
