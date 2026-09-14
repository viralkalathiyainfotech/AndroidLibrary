package com.vc.composecore.modifier

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * Default color tokens and constants for Shimmer / Skeleton loader effects.
 */
object ShimmerDefaults {
    val LightBaseColor = Color(0xFFE2E8F0)
    val LightHighlightColor = Color(0xFFF8FAFC)

    val DarkBaseColor = Color(0xFF1E293B)
    val DarkHighlightColor = Color(0xFF334155)

    const val DEFAULT_DURATION_MS = 1200
}

/**
 * High-performance, diagonal animated Shimmer brush modifier for skeleton loading states.
 * Automatically adapts to system Light and Dark themes.
 *
 * Example:
 * ```kotlin
 * Box(
 *     modifier = Modifier
 *         .size(100.dp, 20.dp)
 *         .shimmer(shape = RoundedCornerShape(4.dp))
 * )
 * ```
 *
 * @param showShimmer When false, suppresses the animation and leaves content unaffected.
 * @param shape Corner clip shape for the shimmer box (default: RoundedCornerShape(8.dp)).
 * @param baseColor Base background tone.
 * @param highlightColor Sweeping bright shimmer tone.
 * @param durationMs Duration of one complete shimmer sweep cycle in milliseconds.
 */
fun Modifier.shimmer(
    showShimmer: Boolean = true,
    shape: Shape = RoundedCornerShape(8.dp),
    baseColor: Color? = null,
    highlightColor: Color? = null,
    durationMs: Int = ShimmerDefaults.DEFAULT_DURATION_MS
): Modifier = composed {
    if (!showShimmer) return@composed this

    val isDark = isSystemInDarkTheme()
    val actualBaseColor = baseColor ?: if (isDark) ShimmerDefaults.DarkBaseColor else ShimmerDefaults.LightBaseColor
    val actualHighlightColor = highlightColor ?: if (isDark) ShimmerDefaults.DarkHighlightColor else ShimmerDefaults.LightHighlightColor

    var size by remember { mutableStateOf(IntSize.Zero) }

    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMs,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslateAnim"
    )

    val brush = if (size.width > 0 && size.height > 0) {
        val width = size.width.toFloat()
        val height = size.height.toFloat()
        val progress = translateAnim / 1000f
        val offset = (width + height) * progress

        Brush.linearGradient(
            colors = listOf(
                actualBaseColor,
                actualHighlightColor,
                actualBaseColor
            ),
            start = Offset(x = offset - width, y = offset - height),
            end = Offset(x = offset, y = offset)
        )
    } else {
        Brush.linearGradient(listOf(actualBaseColor, actualBaseColor))
    }

    this
        .clip(shape)
        .onGloballyPositioned { coordinates ->
            size = coordinates.size
        }
        .background(brush)
}
