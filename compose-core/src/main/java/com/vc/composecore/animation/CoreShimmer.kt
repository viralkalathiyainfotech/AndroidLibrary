package com.vc.composecore.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreTheme

/**
 * Highly efficient Shimmer Brush modifier that animates gradient coordinates
 * without triggering unnecessary recompositions.
 */
fun Modifier.shimmer(
    durationMs: Int = 1200,
    baseColor: Color? = null,
    highlightColor: Color? = null,
    shape: Shape? = null,
    enabled: Boolean = true
): Modifier = composed {
    if (!enabled) return@composed this

    val base = baseColor ?: CoreTheme.colors.shimmerBase
    val highlight = highlightColor ?: CoreTheme.colors.shimmerHighlight

    val transition = rememberInfiniteTransition(label = "CoreShimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CoreShimmerOffset"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            base,
            highlight,
            base
        ),
        start = Offset(translateAnim - 500f, translateAnim - 500f),
        end = Offset(translateAnim, translateAnim)
    )

    val clippedModifier = if (shape != null) this.clip(shape) else this
    clippedModifier.background(brush)
}

@Composable
fun CoreShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(CoreRadius.small),
    baseColor: Color = CoreTheme.colors.shimmerBase,
    highlightColor: Color = CoreTheme.colors.shimmerHighlight
) {
    Box(
        modifier = modifier
            .clip(shape)
            .shimmer(baseColor = baseColor, highlightColor = highlightColor)
    )
}

@Composable
fun CoreShimmerText(
    modifier: Modifier = Modifier,
    width: Dp = 120.dp,
    height: Dp = 16.dp,
    shape: Shape = RoundedCornerShape(CoreRadius.xs)
) {
    CoreShimmerBox(
        modifier = modifier
            .width(width)
            .height(height),
        shape = shape
    )
}

@Composable
fun CoreShimmerList(
    modifier: Modifier = Modifier,
    itemCount: Int = 5,
    itemSpacing: Dp = CoreTheme.spacing.md
) {
    Column(modifier = modifier.fillMaxWidth()) {
        repeat(itemCount) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = itemSpacing / 2)
            ) {
                CoreShimmerBox(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(CoreRadius.small)
                )
                Spacer(modifier = Modifier.width(CoreTheme.spacing.md))
                Column(modifier = Modifier.weight(1f)) {
                    CoreShimmerText(width = 160.dp, height = 16.dp)
                    Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                    CoreShimmerText(width = 100.dp, height = 12.dp)
                }
            }
        }
    }
}
