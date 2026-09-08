package com.vc.androidcore.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

/**
 * Starts an [Activity] of type [T] with optional intent builder lambda and finish flag.
 *
 * Example:
 * ```kotlin
 * openActivity<UserDetailActivity> {
 *     putExtra("user_id", 42)
 * }
 * ```
 */
inline fun <reified T : Activity> Context.openActivity(
    finishCurrent: Boolean = false,
    noinline init: (Intent.() -> Unit)? = null
) {
    val intent = Intent(this, T::class.java).apply {
        init?.invoke(this)
    }
    startActivity(intent)
    if (finishCurrent && this is Activity) {
        finish()
    }
}

/**
 * Starts an [Activity] of type [T] from a [Fragment].
 */
inline fun <reified T : Activity> Fragment.openActivity(
    finishCurrent: Boolean = false,
    noinline init: (Intent.() -> Unit)? = null
) {
    requireContext().openActivity<T>(finishCurrent = finishCurrent && activity != null, init = init)
    if (finishCurrent) {
        activity?.finish()
    }
}

/**
 * Starts an [Activity] and immediately finishes the calling [Activity].
 */
inline fun <reified T : Activity> Activity.openActivityAndFinish(
    noinline init: (Intent.() -> Unit)? = null
) {
    openActivity<T>(finishCurrent = true, init = init)
}
