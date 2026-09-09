package com.vc.composecore.components.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreTheme

@Composable
fun CoreBadge(
    count: Int,
    modifier: Modifier = Modifier,
    maxCount: Int = 99,
    containerColor: Color = CoreTheme.colors.error,
    contentColor: Color = CoreTheme.colors.onError
) {
    val displayString = if (count > maxCount) "$maxCount+" else count.toString()
    Badge(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor
    ) {
        Text(text = displayString, style = CoreTheme.typography.labelSmall)
    }
}

@Composable
fun CoreNotificationBadge(
    modifier: Modifier = Modifier,
    size: Dp = 8.dp,
    color: Color = CoreTheme.colors.error
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color, CircleShape)
    )
}

@Composable
fun CoreStatusBadge(
    label: String,
    modifier: Modifier = Modifier,
    containerColor: Color = CoreTheme.colors.primaryContainer,
    contentColor: Color = CoreTheme.colors.onPrimaryContainer
) {
    Box(
        modifier = modifier
            .background(containerColor, RoundedCornerShape(CoreRadius.full))
            .padding(horizontal = CoreTheme.spacing.sm, vertical = CoreTheme.spacing.xxs),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = CoreTheme.typography.labelSmall,
            color = contentColor
        )
    }
}
