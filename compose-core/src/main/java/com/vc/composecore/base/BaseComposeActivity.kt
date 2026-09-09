package com.vc.composecore.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.vc.composecore.theme.CoreTheme
import com.vc.composecore.theme.ThemeMode

/**
 * Base [ComponentActivity] configuring modern Android edge-to-edge rendering,
 * lifecycle handling, and standard [CoreTheme] integration.
 */
abstract class BaseComposeActivity : ComponentActivity() {

    /**
     * Override to specify light, dark, or system theme mode.
     */
    open val themeMode: ThemeMode = ThemeMode.System

    /**
     * Override to enable Material You dynamic coloring on Android 12+.
     */
    open val dynamicColor: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CoreTheme(
                themeMode = themeMode,
                dynamicColor = dynamicColor
            ) {
                Content()
            }
        }
    }

    /**
     * Primary Composable UI content tree of the activity.
     */
    @Composable
    abstract fun Content()
}
