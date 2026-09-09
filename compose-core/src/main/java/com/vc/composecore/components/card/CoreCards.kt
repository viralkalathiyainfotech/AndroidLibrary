package com.vc.composecore.components.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreTheme

@Composable
fun CoreCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(CoreRadius.medium),
    containerColor: Color = CoreTheme.colors.surface,
    contentColor: Color = CoreTheme.colors.onSurface,
    border: BorderStroke? = null,
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = CoreTheme.elevation.level1),
    contentPadding: Dp = CoreTheme.spacing.md,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor),
        elevation = elevation,
        border = border
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun CoreOutlinedCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(CoreRadius.medium),
    containerColor: Color = CoreTheme.colors.surface,
    contentColor: Color = CoreTheme.colors.onSurface,
    borderColor: Color = CoreTheme.colors.outlineVariant,
    borderWidth: Dp = 1.dp,
    contentPadding: Dp = CoreTheme.spacing.md,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor, contentColor = contentColor),
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun CoreElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(CoreRadius.medium),
    containerColor: Color = CoreTheme.colors.surface,
    contentColor: Color = CoreTheme.colors.onSurface,
    elevation: Dp = CoreTheme.elevation.level2,
    contentPadding: Dp = CoreTheme.spacing.md,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor, contentColor = contentColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun CoreClickableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(CoreRadius.medium),
    containerColor: Color = CoreTheme.colors.surface,
    contentColor: Color = CoreTheme.colors.onSurface,
    elevation: CardElevation = CardDefaults.cardElevation(
        defaultElevation = CoreTheme.elevation.level1,
        pressedElevation = CoreTheme.elevation.level3
    ),
    border: BorderStroke? = null,
    contentPadding: Dp = CoreTheme.spacing.md,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor),
        elevation = elevation,
        border = border
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}
