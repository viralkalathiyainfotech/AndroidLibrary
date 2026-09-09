package com.vc.composecore.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vc.androidcore.network.LiveNetworkMonitor
import com.vc.composecore.theme.CoreTheme

/**
 * Returns a reactive [State<Boolean>] reflecting network connectivity via the core [LiveNetworkMonitor].
 */
@Composable
fun rememberNetworkStatus(): State<Boolean> {
    val context = LocalContext.current
    val monitor = remember(context) { LiveNetworkMonitor(context) }
    val isOnlineState = monitor.isOnline.collectAsStateWithLifecycle()

    DisposableEffect(monitor) {
        onDispose {
            monitor.stop()
        }
    }

    return isOnlineState
}

/**
 * Animated banner that appears smoothly when offline and auto-dismisses when network returns.
 */
@Composable
fun CoreNetworkStatusBanner(
    isOnline: Boolean,
    modifier: Modifier = Modifier,
    offlineMessage: String = "You are currently offline. Check your connection."
) {
    AnimatedVisibility(
        visible = !isOnline,
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CoreTheme.colors.warning)
                .padding(horizontal = CoreTheme.spacing.md, vertical = CoreTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                tint = CoreTheme.colors.onWarning,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(CoreTheme.spacing.xs))
            Text(
                text = offlineMessage,
                style = CoreTheme.typography.bodySmall,
                color = CoreTheme.colors.onWarning
            )
        }
    }
}

@Composable
fun CoreOfflineBanner(modifier: Modifier = Modifier) {
    val isOnline = rememberNetworkStatus()
    CoreNetworkStatusBanner(isOnline = isOnline.value, modifier = modifier)
}
