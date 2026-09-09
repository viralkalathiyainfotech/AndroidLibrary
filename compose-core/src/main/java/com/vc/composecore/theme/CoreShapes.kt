package com.vc.composecore.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Shape definitions built on top of [CoreRadius] design tokens.
 */
@Immutable
data class CoreShapes(
    val none: RoundedCornerShape = RoundedCornerShape(CoreRadius.none),
    val extraSmall: RoundedCornerShape = RoundedCornerShape(CoreRadius.xs),
    val small: RoundedCornerShape = RoundedCornerShape(CoreRadius.small),
    val medium: RoundedCornerShape = RoundedCornerShape(CoreRadius.medium),
    val large: RoundedCornerShape = RoundedCornerShape(CoreRadius.large),
    val extraLarge: RoundedCornerShape = RoundedCornerShape(CoreRadius.xlarge),
    val full: RoundedCornerShape = RoundedCornerShape(CoreRadius.full)
) {
    fun toMaterialShapes(): Shapes = Shapes(
        extraSmall = extraSmall,
        small = small,
        medium = medium,
        large = large,
        extraLarge = extraLarge
    )
}

val LocalCoreShapes = staticCompositionLocalOf { CoreShapes() }
