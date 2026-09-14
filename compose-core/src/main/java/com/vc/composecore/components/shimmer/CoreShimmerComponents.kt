package com.vc.composecore.components.shimmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vc.composecore.modifier.shimmer

/**
 * Basic rectangular or rounded shimmer box for generic placeholder content.
 */
@Composable
fun CoreShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    baseColor: Color? = null,
    highlightColor: Color? = null
) {
    Box(
        modifier = modifier.shimmer(
            shape = shape,
            baseColor = baseColor,
            highlightColor = highlightColor
        )
    )
}

/**
 * Circular shimmer element ideal for profile avatar and circular icon placeholders.
 */
@Composable
fun CoreShimmerCircle(
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
    baseColor: Color? = null,
    highlightColor: Color? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .shimmer(
                shape = CircleShape,
                baseColor = baseColor,
                highlightColor = highlightColor
            )
    )
}

/**
 * Horizontal shimmer bar resembling a line of text in skeleton loaders.
 *
 * @param widthFraction Fraction of available parent width (e.g. 0.8f for 80% width).
 * @param height Height of the text line placeholder (default: 16.dp).
 */
@Composable
fun CoreShimmerTextLine(
    modifier: Modifier = Modifier,
    widthFraction: Float = 1f,
    height: Dp = 16.dp,
    shape: Shape = RoundedCornerShape(4.dp),
    baseColor: Color? = null,
    highlightColor: Color? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .shimmer(
                shape = shape,
                baseColor = baseColor,
                highlightColor = highlightColor
            )
    )
}

/**
 * Standard user card shimmer placeholder featuring an avatar, name, and subtitle skeleton.
 */
@Composable
fun CoreShimmerCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    avatarSize: Dp = 48.dp
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CoreShimmerCircle(size = avatarSize)
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CoreShimmerTextLine(widthFraction = 0.7f, height = 16.dp)
                CoreShimmerTextLine(widthFraction = 0.4f, height = 12.dp)
            }
        }
    }
}

/**
 * Vertical list of shimmer items useful as a placeholder while loading feed or list data.
 *
 * @param count Number of skeleton items to display.
 * @param spacing Space between adjacent shimmer items.
 * @param itemContent Custom composable to repeat, defaults to [CoreShimmerCard].
 */
@Composable
fun CoreShimmerList(
    count: Int = 5,
    modifier: Modifier = Modifier,
    spacing: Dp = 12.dp,
    itemContent: @Composable () -> Unit = { CoreShimmerCard() }
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(count) {
            itemContent()
        }
    }
}
