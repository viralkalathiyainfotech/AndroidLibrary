package com.vc.composecore.components.snackbar

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.vc.composecore.state.UiEffect
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreTheme

@Composable
fun CoreSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    type: UiEffect.SnackbarType = UiEffect.SnackbarType.Info
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data ->
        CoreSnackbar(data = data, type = type)
    }
}

@Composable
fun CoreSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
    type: UiEffect.SnackbarType = UiEffect.SnackbarType.Info
) {
    val containerColor = when (type) {
        UiEffect.SnackbarType.Success -> CoreTheme.colors.success
        UiEffect.SnackbarType.Error -> CoreTheme.colors.error
        UiEffect.SnackbarType.Warning -> CoreTheme.colors.warning
        UiEffect.SnackbarType.Info -> CoreTheme.colors.surfaceVariant
    }

    val contentColor = when (type) {
        UiEffect.SnackbarType.Success -> CoreTheme.colors.onSuccess
        UiEffect.SnackbarType.Error -> CoreTheme.colors.onError
        UiEffect.SnackbarType.Warning -> CoreTheme.colors.onWarning
        UiEffect.SnackbarType.Info -> CoreTheme.colors.onSurfaceVariant
    }

    Snackbar(
        modifier = modifier.padding(CoreTheme.spacing.md),
        shape = RoundedCornerShape(CoreRadius.small),
        containerColor = containerColor,
        contentColor = contentColor,
        action = data.visuals.actionLabel?.let { actionText ->
            {
                TextButton(
                    onClick = { data.performAction() },
                    colors = ButtonDefaults.textButtonColors(contentColor = contentColor)
                ) {
                    Text(actionText, style = CoreTheme.typography.button)
                }
            }
        }
    ) {
        Text(text = data.visuals.message, style = CoreTheme.typography.bodyMedium)
    }
}
