package com.vc.composecore.components.divider

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vc.composecore.theme.CoreTheme

@Composable
fun CoreDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = CoreTheme.colors.divider
) {
    HorizontalDivider(
        modifier = modifier.fillMaxWidth(),
        thickness = thickness,
        color = color
    )
}

@Composable
fun CoreVerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = CoreTheme.colors.divider
) {
    VerticalDivider(
        modifier = modifier.fillMaxHeight(),
        thickness = thickness,
        color = color
    )
}

@Composable
fun CoreListSeparator(
    modifier: Modifier = Modifier,
    indent: Dp = CoreTheme.spacing.md
) {
    CoreDivider(
        modifier = modifier.padding(start = indent),
        color = CoreTheme.colors.divider.copy(alpha = 0.6f)
    )
}
