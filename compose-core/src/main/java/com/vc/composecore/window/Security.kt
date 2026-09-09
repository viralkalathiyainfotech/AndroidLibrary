package com.vc.composecore.window

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vc.composecore.theme.CoreTheme

/**
 * Attaches [WindowManager.LayoutParams.FLAG_SECURE] to the hosting window for the duration
 * of this composable's composition, blocking screenshots, screen recording, and recent app snapshots.
 */
@Composable
fun SecureScreen(
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val window = (context as? Activity)?.window

    DisposableEffect(enabled, window) {
        if (enabled && window != null) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        onDispose {
            if (enabled && window != null) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }

    content()
}

/**
 * Obscures sensitive visual data (e.g. credit card number, balances) when hidden.
 */
@Composable
fun SensitiveContent(
    isSensitive: Boolean,
    modifier: Modifier = Modifier,
    blurRadius: Int = 16,
    maskText: String = "••••••••",
    content: @Composable () -> Unit
) {
    if (isSensitive) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.blur(blurRadius.dp)) {
                content()
            }
            Text(
                text = maskText,
                style = CoreTheme.typography.titleMedium,
                color = CoreTheme.colors.onSurfaceVariant
            )
        }
    } else {
        content()
    }
}
