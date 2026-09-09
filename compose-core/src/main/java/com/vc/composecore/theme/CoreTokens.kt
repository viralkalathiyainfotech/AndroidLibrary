package com.vc.composecore.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Production-ready Design Token System providing centralized metrics for
 * Spacing, Corner Radius, Elevation, Border Width, Icon Size, Component Heights, and Animation Durations.
 */
@Immutable
object CoreSpacing {
    val none: Dp = 0.dp
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val xxl: Dp = 48.dp
    val xxxl: Dp = 64.dp
}

@Immutable
object CoreRadius {
    val none: Dp = 0.dp
    val xs: Dp = 4.dp
    val small: Dp = 8.dp
    val medium: Dp = 12.dp
    val large: Dp = 16.dp
    val xlarge: Dp = 24.dp
    val xxlarge: Dp = 32.dp
    val full: Dp = 9999.dp
}

@Immutable
object CoreElevation {
    val level0: Dp = 0.dp
    val level1: Dp = 1.dp
    val level2: Dp = 3.dp
    val level3: Dp = 6.dp
    val level4: Dp = 8.dp
    val level5: Dp = 12.dp
}

@Immutable
object CoreBorderWidth {
    val hairline: Dp = 0.5.dp
    val thin: Dp = 1.dp
    val medium: Dp = 2.dp
    val thick: Dp = 3.dp
}

@Immutable
object CoreIconSize {
    val xs: Dp = 16.dp
    val sm: Dp = 20.dp
    val md: Dp = 24.dp
    val lg: Dp = 32.dp
    val xl: Dp = 40.dp
    val xxl: Dp = 48.dp
}

@Immutable
object CoreComponentHeight {
    val buttonSmall: Dp = 36.dp
    val buttonMedium: Dp = 44.dp
    val buttonLarge: Dp = 52.dp
    val textField: Dp = 56.dp
    val topAppBar: Dp = 64.dp
    val bottomBar: Dp = 80.dp
    val listItem: Dp = 56.dp
    val chip: Dp = 32.dp
    val progressBar: Dp = 4.dp
}

@Immutable
object CoreAnimationDuration {
    const val fast: Int = 150
    const val normal: Int = 300
    const val slow: Int = 500
    const val extraSlow: Int = 800
}
