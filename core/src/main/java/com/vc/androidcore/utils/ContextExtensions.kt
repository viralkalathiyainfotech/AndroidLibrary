package com.vc.androidcore.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

/**
 * Context extension functions for common operations.
 */
fun Context.showToast(message: String, isLong: Boolean = false) {
    Toast.makeText(this, message, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun Context.getColorCompat(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun Context.getColorStateListCompat(@ColorRes id: Int): ColorStateList? {
    return ContextCompat.getColorStateList(this, id)
}

fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}

fun Context.getStringCompat(@StringRes id: Int, vararg formatArgs: Any): String {
    return getString(id, *formatArgs)
}

fun Context.getDimensionPixelSize(@DimenRes id: Int): Int {
    return resources.getDimensionPixelSize(id)
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

/**
 * Resource utility class for Java interoperability.
 */
object ResourceUtils {
    fun getColor(context: Context, @ColorRes colorResId: Int): Int = context.getColorCompat(colorResId)
    fun getDrawable(context: Context, @DrawableRes drawableResId: Int): Drawable? = context.getDrawableCompat(drawableResId)
    fun getString(context: Context, @StringRes stringResId: Int, vararg args: Any): String = context.getStringCompat(stringResId, *args)
}
