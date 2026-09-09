package com.vc.composecore.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

enum class ThemeMode {
    Light,
    Dark,
    System
}

/**
 * Enterprise Jetpack Compose Theme foundation supporting Light, Dark, System modes,
 * Material You dynamic coloring, and deep customization without hardcoded branding.
 */
@Composable
fun CoreTheme(
    themeMode: ThemeMode = ThemeMode.System,
    dynamicColor: Boolean = false,
    colors: CoreColors? = null,
    typography: CoreTypography = CoreTypography(),
    shapes: CoreShapes = CoreShapes(),
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    val resolvedColors = colors ?: if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val dynamicScheme = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        val basePalette = if (darkTheme) CoreDarkColors else CoreLightColors
        basePalette.copy(
            primary = dynamicScheme.primary,
            onPrimary = dynamicScheme.onPrimary,
            primaryContainer = dynamicScheme.primaryContainer,
            onPrimaryContainer = dynamicScheme.onPrimaryContainer,
            secondary = dynamicScheme.secondary,
            onSecondary = dynamicScheme.onSecondary,
            background = dynamicScheme.background,
            onBackground = dynamicScheme.onBackground,
            surface = dynamicScheme.surface,
            onSurface = dynamicScheme.onSurface,
            outline = dynamicScheme.outline,
            isDark = darkTheme
        )
    } else {
        if (darkTheme) CoreDarkColors else CoreLightColors
    }

    CompositionLocalProvider(
        LocalCoreColors provides resolvedColors,
        LocalCoreTypography provides typography,
        LocalCoreShapes provides shapes
    ) {
        MaterialTheme(
            colorScheme = resolvedColors.toMaterialColorScheme(),
            typography = typography.toMaterialTypography(),
            shapes = shapes.toMaterialShapes(),
            content = content
        )
    }
}

/**
 * Direct accessor object for design tokens, colors, typography, and shapes.
 */
object CoreTheme {
    val colors: CoreColors
        @Composable
        @ReadOnlyComposable
        get() = LocalCoreColors.current

    val typography: CoreTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalCoreTypography.current

    val shapes: CoreShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalCoreShapes.current

    val spacing: CoreSpacing get() = CoreSpacing
    val radius: CoreRadius get() = CoreRadius
    val elevation: CoreElevation get() = CoreElevation
    val borderWidth: CoreBorderWidth get() = CoreBorderWidth
    val iconSize: CoreIconSize get() = CoreIconSize
    val componentHeight: CoreComponentHeight get() = CoreComponentHeight
    val animationDuration: CoreAnimationDuration get() = CoreAnimationDuration
}
