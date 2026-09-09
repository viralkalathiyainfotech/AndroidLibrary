package com.vc.composecore.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreTheme

/**
 * Clean analytics tracker abstraction decoupling UI from third-party SDKs (Firebase, Mixpanel, etc.).
 */
interface ScreenTracker {
    fun trackScreen(name: String, parameters: Map<String, Any> = emptyMap())
}

val LocalScreenTracker = staticCompositionLocalOf<ScreenTracker> {
    object : ScreenTracker {
        override fun trackScreen(name: String, parameters: Map<String, Any>) {}
    }
}

@Composable
fun TrackScreenEffect(
    screenName: String,
    parameters: Map<String, Any> = emptyMap(),
    tracker: ScreenTracker = LocalScreenTracker.current
) {
    LaunchedEffect(screenName) {
        tracker.trackScreen(screenName, parameters)
    }
}

/**
 * Crash reporting abstraction.
 */
interface CrashReporter {
    fun recordException(throwable: Throwable, message: String? = null)
    fun log(message: String)
}

val LocalCrashReporter = staticCompositionLocalOf<CrashReporter> {
    object : CrashReporter {
        override fun recordException(throwable: Throwable, message: String?) {}
        override fun log(message: String) {}
    }
}

/**
 * Feature flag manager abstraction.
 */
interface FeatureFlagManager {
    fun isEnabled(flagKey: String, default: Boolean = false): Boolean
    fun getString(flagKey: String, default: String = ""): String
    fun getBoolean(flagKey: String, default: Boolean = false): Boolean = isEnabled(flagKey, default)
    fun getInt(flagKey: String, default: Int = 0): Int
}

val LocalFeatureFlags = staticCompositionLocalOf<FeatureFlagManager> {
    object : FeatureFlagManager {
        override fun isEnabled(flagKey: String, default: Boolean): Boolean = default
        override fun getString(flagKey: String, default: String): String = default
        override fun getInt(flagKey: String, default: Int): Int = default
    }
}

/**
 * Environment configuration.
 */
enum class CoreEnvironment {
    DEV,
    QA,
    STAGING,
    PRODUCTION
}

data class EnvironmentConfig(
    val environment: CoreEnvironment = CoreEnvironment.DEV,
    val baseUrl: String = "",
    val enableLogging: Boolean = true,
    val enableAnalytics: Boolean = false
)

val LocalEnvironmentConfig = staticCompositionLocalOf { EnvironmentConfig() }

/**
 * Debug overlay for inspecting app runtime properties in debug builds.
 */
@Composable
fun CoreDebugOverlay(
    environment: CoreEnvironment,
    appVersion: String = "1.0.0",
    isOnline: Boolean = true,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        if (enabled) {
            Box(
                modifier = Modifier
                    .padding(CoreTheme.spacing.md)
                    .align(Alignment.BottomEnd)
            ) {
                Surface(
                    onClick = { isExpanded = true },
                    shape = CircleShape,
                    color = CoreTheme.colors.primary,
                    shadowElevation = CoreTheme.elevation.level3,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = "Debug overlay",
                            tint = CoreTheme.colors.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            if (isExpanded) {
                Dialog(onDismissRequest = { isExpanded = false }) {
                    Card(
                        shape = RoundedCornerShape(CoreRadius.large),
                        colors = CardDefaults.cardColors(containerColor = CoreTheme.colors.surface)
                    ) {
                        Column(modifier = Modifier.padding(CoreTheme.spacing.lg)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Debug Diagnostics",
                                    style = CoreTheme.typography.titleLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { isExpanded = false }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close")
                                }
                            }
                            Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
                            Text(text = "Environment: ${environment.name}", style = CoreTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                            Text(text = "App Version: $appVersion", style = CoreTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
                            Text(text = "Network Status: ${if (isOnline) "Connected" else "Offline"}", style = CoreTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
