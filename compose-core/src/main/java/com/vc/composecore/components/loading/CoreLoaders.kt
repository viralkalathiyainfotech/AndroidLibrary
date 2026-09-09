package com.vc.composecore.components.loading

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vc.composecore.theme.CoreTheme

@Composable
fun CoreCircularProgress(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    strokeWidth: Dp = 3.5.dp,
    color: Color = CoreTheme.colors.primary,
    trackColor: Color = CoreTheme.colors.primaryContainer
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = color,
        trackColor = trackColor,
        strokeWidth = strokeWidth,
        strokeCap = StrokeCap.Round
    )
}

@Composable
fun CoreLinearProgress(
    modifier: Modifier = Modifier,
    color: Color = CoreTheme.colors.primary,
    trackColor: Color = CoreTheme.colors.primaryContainer
) {
    LinearProgressIndicator(
        modifier = modifier
            .fillMaxWidth()
            .height(CoreTheme.componentHeight.progressBar),
        color = color,
        trackColor = trackColor,
        strokeCap = StrokeCap.Round
    )
}

@Composable
fun CoreLoadingOverlay(
    modifier: Modifier = Modifier,
    message: String? = null,
    backgroundColor: Color = CoreTheme.colors.background.copy(alpha = 0.85f),
    indicatorColor: Color = CoreTheme.colors.primary
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(CoreTheme.spacing.md)
        ) {
            CoreCircularProgress(color = indicatorColor)
            if (message != null) {
                Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
                Text(
                    text = message,
                    style = CoreTheme.typography.bodyMedium,
                    color = CoreTheme.colors.onBackground
                )
            }
        }
    }
}

@Composable
fun CoreLoadingContent(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    loadingOverlay: @Composable () -> Unit = { CoreLoadingOverlay() },
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            loadingOverlay()
        }
    }
}
