package com.vc.composecore.responsive

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowWidthClass {
    Compact,   // < 600dp (standard portrait phones)
    Medium,    // 600dp - 840dp (tablets, foldables, landscape phones)
    Expanded   // >= 840dp (large tablets, desktop windows)
}

enum class WindowHeightClass {
    Compact,   // < 480dp (landscape phone)
    Medium,    // 480dp - 900dp (standard portrait)
    Expanded   // >= 900dp (tall screens / large tablets)
}

enum class DeviceOrientation {
    Portrait,
    Landscape
}

@Immutable
data class CoreWindowSize(
    val widthClass: WindowWidthClass,
    val heightClass: WindowHeightClass,
    val widthDp: Dp,
    val heightDp: Dp,
    val orientation: DeviceOrientation
) {
    val isCompact: Boolean get() = widthClass == WindowWidthClass.Compact
    val isMedium: Boolean get() = widthClass == WindowWidthClass.Medium
    val isExpanded: Boolean get() = widthClass == WindowWidthClass.Expanded
    val isTabletOrExpanded: Boolean get() = isMedium || isExpanded
    val isLandscape: Boolean get() = orientation == DeviceOrientation.Landscape
}

val LocalCoreWindowSize = staticCompositionLocalOf {
    CoreWindowSize(
        widthClass = WindowWidthClass.Compact,
        heightClass = WindowHeightClass.Medium,
        widthDp = 360.dp,
        heightDp = 640.dp,
        orientation = DeviceOrientation.Portrait
    )
}

@Composable
fun rememberCoreWindowSize(): CoreWindowSize {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp.dp
    val heightDp = configuration.screenHeightDp.dp

    val widthClass = when {
        widthDp < 600.dp -> WindowWidthClass.Compact
        widthDp < 840.dp -> WindowWidthClass.Medium
        else -> WindowWidthClass.Expanded
    }

    val heightClass = when {
        heightDp < 480.dp -> WindowHeightClass.Compact
        heightDp < 900.dp -> WindowHeightClass.Medium
        else -> WindowHeightClass.Expanded
    }

    val orientation = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        DeviceOrientation.Landscape
    } else {
        DeviceOrientation.Portrait
    }

    return remember(widthDp, heightDp, orientation) {
        CoreWindowSize(
            widthClass = widthClass,
            heightClass = heightClass,
            widthDp = widthDp,
            heightDp = heightDp,
            orientation = orientation
        )
    }
}

/**
 * High-level layout component rendering appropriate UI branches based on screen size class.
 */
@Composable
fun CoreResponsiveLayout(
    compact: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    medium: (@Composable () -> Unit)? = null,
    expanded: (@Composable () -> Unit)? = null
) {
    val windowSize = rememberCoreWindowSize()

    CompositionLocalProvider(LocalCoreWindowSize provides windowSize) {
        Box(modifier = modifier) {
            when (windowSize.widthClass) {
                WindowWidthClass.Compact -> compact()
                WindowWidthClass.Medium -> (medium ?: compact)()
                WindowWidthClass.Expanded -> (expanded ?: medium ?: compact)()
            }
        }
    }
}
