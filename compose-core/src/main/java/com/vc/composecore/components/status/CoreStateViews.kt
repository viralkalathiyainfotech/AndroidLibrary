package com.vc.composecore.components.status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vc.composecore.theme.CoreTheme

@Composable
fun CoreEmptyView(
    modifier: Modifier = Modifier,
    title: String = "No Data Found",
    message: String = "There are currently no items available to display.",
    icon: ImageVector = Icons.Default.Inbox,
    iconTint: Color = CoreTheme.colors.outline,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(CoreTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(64.dp),
            tint = iconTint
        )
        Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
        Text(
            text = title,
            style = CoreTheme.typography.titleLarge,
            color = CoreTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
        Text(
            text = message,
            style = CoreTheme.typography.bodyMedium,
            color = CoreTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CoreTheme.colors.primary,
                    contentColor = CoreTheme.colors.onPrimary
                )
            ) {
                Text(actionLabel, style = CoreTheme.typography.button)
            }
        }
    }
}

@Composable
fun CoreErrorView(
    modifier: Modifier = Modifier,
    title: String = "Something Went Wrong",
    message: String = "An unexpected error occurred. Please try again.",
    icon: ImageVector = Icons.Default.ErrorOutline,
    iconTint: Color = CoreTheme.colors.error,
    retryLabel: String = "Try Again",
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(CoreTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(64.dp),
            tint = iconTint
        )
        Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
        Text(
            text = title,
            style = CoreTheme.typography.titleLarge,
            color = CoreTheme.colors.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
        Text(
            text = message,
            style = CoreTheme.typography.bodyMedium,
            color = CoreTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CoreTheme.colors.primary,
                    contentColor = CoreTheme.colors.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(CoreTheme.spacing.xs))
                Text(retryLabel, style = CoreTheme.typography.button)
            }
        }
    }
}

@Composable
fun CoreNoInternetView(
    modifier: Modifier = Modifier,
    title: String = "No Internet Connection",
    message: String = "Please check your network connection and try again.",
    icon: ImageVector = Icons.Default.CloudOff,
    retryLabel: String = "Retry Connection",
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(CoreTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(64.dp),
            tint = CoreTheme.colors.warning
        )
        Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
        Text(
            text = title,
            style = CoreTheme.typography.titleLarge,
            color = CoreTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
        Text(
            text = message,
            style = CoreTheme.typography.bodyMedium,
            color = CoreTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CoreTheme.colors.primary,
                    contentColor = CoreTheme.colors.onPrimary
                )
            ) {
                Text(retryLabel, style = CoreTheme.typography.button)
            }
        }
    }
}

@Composable
fun CoreUnauthorizedView(
    modifier: Modifier = Modifier,
    title: String = "Session Expired",
    message: String = "Your session has expired. Please sign in again to continue.",
    icon: ImageVector = Icons.Default.Lock,
    actionLabel: String = "Sign In",
    onLoginClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(CoreTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(64.dp),
            tint = CoreTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
        Text(
            text = title,
            style = CoreTheme.typography.titleLarge,
            color = CoreTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
        Text(
            text = message,
            style = CoreTheme.typography.bodyMedium,
            color = CoreTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (onLoginClick != null) {
            Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
            Button(onClick = onLoginClick) {
                Text(actionLabel, style = CoreTheme.typography.button)
            }
        }
    }
}

@Composable
fun CoreMaintenanceView(
    modifier: Modifier = Modifier,
    title: String = "Under Scheduled Maintenance",
    message: String = "We are currently improving our services. Please check back shortly.",
    icon: ImageVector = Icons.Default.Construction
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(CoreTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(64.dp),
            tint = CoreTheme.colors.warning
        )
        Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
        Text(
            text = title,
            style = CoreTheme.typography.titleLarge,
            color = CoreTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
        Text(
            text = message,
            style = CoreTheme.typography.bodyMedium,
            color = CoreTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CoreRetryView(
    modifier: Modifier = Modifier,
    message: String = "Failed to load content",
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier.padding(CoreTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = CoreTheme.typography.bodySmall,
            color = CoreTheme.colors.error
        )
        Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
        OutlinedButton(onClick = onRetry) {
            Text("Retry", style = CoreTheme.typography.button)
        }
    }
}
