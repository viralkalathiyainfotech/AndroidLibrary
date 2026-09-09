package com.vc.composecore.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoreDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit
) {
    if (visible) {
        BasicAlertDialog(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            properties = properties
        ) {
            Surface(
                shape = RoundedCornerShape(CoreRadius.large),
                color = CoreTheme.colors.surface,
                tonalElevation = CoreTheme.elevation.level3
            ) {
                content()
            }
        }
    }
}

@Composable
fun CoreAlertDialog(
    visible: Boolean,
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButtonText: String = "OK",
    onConfirm: () -> Unit = onDismissRequest,
    dismissButtonText: String? = null,
    onDismiss: (() -> Unit)? = null,
    icon: ImageVector? = null
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            icon = icon?.let { { Icon(imageVector = it, contentDescription = null, tint = CoreTheme.colors.primary) } },
            title = { Text(text = title, style = CoreTheme.typography.titleLarge) },
            text = { Text(text = message, style = CoreTheme.typography.bodyMedium) },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text(confirmButtonText, style = CoreTheme.typography.button)
                }
            },
            dismissButton = if (dismissButtonText != null && onDismiss != null) {
                {
                    TextButton(onClick = onDismiss) {
                        Text(dismissButtonText, style = CoreTheme.typography.button)
                    }
                }
            } else null,
            shape = RoundedCornerShape(CoreRadius.large)
        )
    }
}

@Composable
fun CoreConfirmDialog(
    visible: Boolean,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    isDanger: Boolean = false,
    icon: ImageVector? = null
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            modifier = modifier,
            icon = icon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (isDanger) CoreTheme.colors.error else CoreTheme.colors.primary
                    )
                }
            },
            title = { Text(title, style = CoreTheme.typography.titleLarge) },
            text = { Text(message, style = CoreTheme.typography.bodyMedium) },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = if (isDanger) ButtonDefaults.buttonColors(containerColor = CoreTheme.colors.error) else ButtonDefaults.buttonColors()
                ) {
                    Text(confirmText, style = CoreTheme.typography.button)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismiss) {
                    Text(dismissText, style = CoreTheme.typography.button)
                }
            },
            shape = RoundedCornerShape(CoreRadius.large)
        )
    }
}

@Composable
fun CoreLoadingDialog(
    visible: Boolean,
    modifier: Modifier = Modifier,
    message: String = "Please wait..."
) {
    CoreDialog(
        visible = visible,
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Row(
            modifier = modifier
                .padding(CoreTheme.spacing.lg)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CoreTheme.spacing.md)
        ) {
            CircularProgressIndicator(
                color = CoreTheme.colors.primary,
                modifier = Modifier.size(32.dp)
            )
            Text(text = message, style = CoreTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun CoreErrorDialog(
    visible: Boolean,
    title: String = "Error",
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    retryText: String? = null,
    onRetry: (() -> Unit)? = null
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            modifier = modifier,
            icon = { Icon(Icons.Default.Error, contentDescription = null, tint = CoreTheme.colors.error) },
            title = { Text(title, style = CoreTheme.typography.titleLarge) },
            text = { Text(message, style = CoreTheme.typography.bodyMedium) },
            confirmButton = {
                if (retryText != null && onRetry != null) {
                    Button(onClick = onRetry) {
                        Text(retryText)
                    }
                } else {
                    Button(onClick = onDismiss) {
                        Text("OK")
                    }
                }
            },
            dismissButton = if (retryText != null && onRetry != null) {
                {
                    TextButton(onClick = onDismiss) {
                        Text("Dismiss")
                    }
                }
            } else null,
            shape = RoundedCornerShape(CoreRadius.large)
        )
    }
}

@Composable
fun CoreSuccessDialog(
    visible: Boolean,
    title: String = "Success",
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String = "Continue"
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            modifier = modifier,
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CoreTheme.colors.success) },
            title = { Text(title, style = CoreTheme.typography.titleLarge) },
            text = { Text(message, style = CoreTheme.typography.bodyMedium) },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text(buttonText)
                }
            },
            shape = RoundedCornerShape(CoreRadius.large)
        )
    }
}

@Composable
fun CoreCustomDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    CoreDialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        content = content
    )
}
