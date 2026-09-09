package com.vc.composecore.permissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vc.composecore.theme.CoreTheme

enum class CorePermissionStatus {
    Granted,
    Denied,
    PermanentlyDenied
}

@Composable
fun rememberCorePermissionState(
    permission: String,
    onPermissionResult: ((Boolean) -> Unit)? = null
): CorePermissionState {
    val context = LocalContext.current
    val activity = context as? Activity

    var status by remember {
        mutableStateOf(
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                CorePermissionStatus.Granted
            } else {
                CorePermissionStatus.Denied
            }
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        status = when {
            isGranted -> CorePermissionStatus.Granted
            activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                CorePermissionStatus.PermanentlyDenied
            }
            else -> CorePermissionStatus.Denied
        }
        onPermissionResult?.invoke(isGranted)
    }

    return remember(permission, status) {
        CorePermissionState(
            permission = permission,
            status = status,
            launchPermissionRequest = { launcher.launch(permission) },
            openAppSettings = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        )
    }
}

class CorePermissionState(
    val permission: String,
    val status: CorePermissionStatus,
    val launchPermissionRequest: () -> Unit,
    val openAppSettings: () -> Unit
) {
    val isGranted: Boolean get() = status == CorePermissionStatus.Granted
    val shouldShowRationale: Boolean get() = status == CorePermissionStatus.Denied
    val isPermanentlyDenied: Boolean get() = status == CorePermissionStatus.PermanentlyDenied
}

@Composable
fun CorePermissionDialog(
    visible: Boolean,
    title: String,
    rationale: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Grant Permission",
    dismissText: String = "Cancel"
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = { Icon(Icons.Default.Security, contentDescription = null, tint = CoreTheme.colors.primary) },
            title = { Text(title, style = CoreTheme.typography.titleLarge) },
            text = { Text(rationale, style = CoreTheme.typography.bodyMedium) },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
            }
        )
    }
}

@Composable
fun CorePermissionDeniedView(
    title: String = "Permission Required",
    message: String = "This feature requires permissions that were permanently denied. Please enable them in system settings.",
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(CoreTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
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
        Spacer(modifier = Modifier.height(CoreTheme.spacing.md))
        Button(onClick = onOpenSettings) {
            Text("Open Settings")
        }
    }
}

@Composable
fun CorePermissionHandler(
    permissionState: CorePermissionState,
    rationaleTitle: String = "Permission Request",
    rationaleMessage: String = "This permission is needed to continue.",
    content: @Composable () -> Unit
) {
    var showRationale by remember { mutableStateOf(false) }

    when {
        permissionState.isGranted -> {
            content()
        }
        permissionState.isPermanentlyDenied -> {
            CorePermissionDeniedView(onOpenSettings = permissionState.openAppSettings)
        }
        else -> {
            if (showRationale) {
                CorePermissionDialog(
                    visible = true,
                    title = rationaleTitle,
                    rationale = rationaleMessage,
                    onConfirm = {
                        showRationale = false
                        permissionState.launchPermissionRequest()
                    },
                    onDismiss = { showRationale = false }
                )
            }
            LaunchedEffect(Unit) {
                if (permissionState.shouldShowRationale) {
                    showRationale = true
                } else {
                    permissionState.launchPermissionRequest()
                }
            }
        }
    }
}
