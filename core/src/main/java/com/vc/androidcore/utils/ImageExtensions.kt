package com.vc.androidcore.utils

import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.dispose
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation

/**
 * Image loading extensions powered by Coil.
 */
fun ImageView.loadImage(
    data: Any?,
    @DrawableRes placeholderRes: Int? = null,
    @DrawableRes errorRes: Int? = null,
    isCircle: Boolean = false,
    cornerRadiusPx: Float = 0f
) {
    this.load(data) {
        crossfade(true)
        placeholderRes?.let { placeholder(it) }
        errorRes?.let { error(it) }
        if (isCircle) {
            transformations(CircleCropTransformation())
        } else if (cornerRadiusPx > 0f) {
            transformations(RoundedCornersTransformation(cornerRadiusPx))
        }
    }
}

/**
 * Loads an image with circular cropping (ideal for avatars).
 */
fun ImageView.loadCircle(
    data: Any?,
    @DrawableRes placeholderRes: Int? = null,
    @DrawableRes errorRes: Int? = null
) {
    loadImage(
        data = data,
        placeholderRes = placeholderRes,
        errorRes = errorRes,
        isCircle = true
    )
}

/**
 * Loads an image with rounded corner radius in pixels or DP.
 */
fun ImageView.loadRounded(
    data: Any?,
    cornerRadiusPx: Float = 0f,
    cornerRadiusDp: Int? = null,
    @DrawableRes placeholderRes: Int? = null,
    @DrawableRes errorRes: Int? = null
) {
    val finalPx = cornerRadiusDp?.let { dp ->
        dp * resources.displayMetrics.density
    } ?: cornerRadiusPx

    loadImage(
        data = data,
        placeholderRes = placeholderRes,
        errorRes = errorRes,
        cornerRadiusPx = finalPx
    )
}

/**
 * Cancels active image requests and clears the image view.
 */
fun ImageView.clearImage() {
    this.dispose()
    this.setImageDrawable(null)
}
