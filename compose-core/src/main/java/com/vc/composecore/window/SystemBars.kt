package com.vc.composecore.window

import android.app.Activity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.core.view.WindowCompat

/**
 * Side effect adjusting system bar icons appearance (dark icons for light surfaces, light icons for dark surfaces).
 */
@Composable
fun SystemBarEffect(
    isDarkIcons: Boolean
) {
    val view = LocalView.current
    DisposableEffect(isDarkIcons) {
        val window = (view.context as? Activity)?.window
        if (window != null) {
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = isDarkIcons
            insetsController.isAppearanceLightNavigationBars = isDarkIcons
        }
        onDispose {}
    }
}

/**
 * Returns current status bar height in Dp.
 */
@Composable
fun statusBarHeight(): Dp {
    return WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
}

/**
 * Returns current navigation bar height in Dp.
 */
@Composable
fun navigationBarHeight(): Dp {
    return WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
}
