package com.vc.androidcore.utils

import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation

/**
 * Image loading extension powered by Coil.
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
