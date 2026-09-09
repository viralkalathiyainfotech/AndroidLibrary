package com.vc.composecore.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vc.composecore.modifier.debouncedClickable
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreTheme
import kotlinx.coroutines.launch

@Composable
fun CoreButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fullWidth: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(CoreRadius.small),
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = CoreTheme.colors.primary,
        contentColor = CoreTheme.colors.onPrimary,
        disabledContainerColor = CoreTheme.colors.outlineVariant,
        disabledContentColor = CoreTheme.colors.outline
    ),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    height: Dp = CoreTheme.componentHeight.buttonMedium,
    debounceMs: Long = 500L
) {
    val widthModifier = if (fullWidth) Modifier.fillMaxWidth() else Modifier
    Button(
        onClick = onClick,
        modifier = modifier
            .then(widthModifier)
            .height(height)
            .semantics { role = Role.Button },
        enabled = enabled && !isLoading,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = colors.contentColor,
                strokeWidth = 2.5.dp,
                strokeCap = StrokeCap.Round
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    androidx.compose.material3.Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(CoreTheme.spacing.xs))
                }
                Text(text = text, style = CoreTheme.typography.button)
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(CoreTheme.spacing.xs))
                    androidx.compose.material3.Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CoreOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fullWidth: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(CoreRadius.small),
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = CoreTheme.colors.primary
    ),
    border: BorderStroke? = BorderStroke(1.dp, if (enabled) CoreTheme.colors.outline else CoreTheme.colors.outlineVariant),
    height: Dp = CoreTheme.componentHeight.buttonMedium
) {
    val widthModifier = if (fullWidth) Modifier.fillMaxWidth() else Modifier
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .then(widthModifier)
            .height(height)
            .semantics { role = Role.Button },
        enabled = enabled && !isLoading,
        shape = shape,
        colors = colors,
        border = border
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = colors.contentColor,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    androidx.compose.material3.Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(CoreTheme.spacing.xs))
                }
                Text(text = text, style = CoreTheme.typography.button)
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(CoreTheme.spacing.xs))
                    androidx.compose.material3.Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CoreTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    colors: ButtonColors = ButtonDefaults.textButtonColors(
        contentColor = CoreTheme.colors.primary
    )
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        enabled = enabled && !isLoading,
        colors = colors
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = colors.contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingIcon != null) {
                    androidx.compose.material3.Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(CoreTheme.spacing.xs))
                }
                Text(text = text, style = CoreTheme.typography.button)
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(CoreTheme.spacing.xs))
                    androidx.compose.material3.Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CoreIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(
        contentColor = CoreTheme.colors.onSurface
    )
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        enabled = enabled,
        colors = colors
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(CoreTheme.iconSize.md)
        )
    }
}

@Composable
fun CoreFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = CoreTheme.colors.primaryContainer,
    contentColor: Color = CoreTheme.colors.onPrimaryContainer,
    shape: Shape = RoundedCornerShape(CoreRadius.large),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    content: @Composable () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = elevation,
        content = content
    )
}

@Composable
fun CoreExtendedFAB(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    containerColor: Color = CoreTheme.colors.primary,
    contentColor: Color = CoreTheme.colors.onPrimary
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        expanded = expanded,
        icon = {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        text = { Text(text, style = CoreTheme.typography.button) },
        containerColor = containerColor,
        contentColor = contentColor
    )
}

@Composable
fun CoreLoadingButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fullWidth: Boolean = false
) {
    CoreButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isLoading = isLoading,
        fullWidth = fullWidth
    )
}

/**
 * Safely manages an asynchronous suspending click action, showing loading and preventing concurrent executions.
 */
@Composable
fun CoreAsyncButton(
    text: String,
    onClick: suspend () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fullWidth: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    CoreButton(
        text = text,
        onClick = {
            if (!isLoading && enabled) {
                isLoading = true
                scope.launch {
                    try {
                        onClick()
                    } finally {
                        isLoading = false
                    }
                }
            }
        },
        modifier = modifier,
        enabled = enabled && !isLoading,
        isLoading = isLoading,
        fullWidth = fullWidth,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    )
}

@Composable
fun CoreDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fullWidth: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    CoreButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isLoading = isLoading,
        fullWidth = fullWidth,
        leadingIcon = leadingIcon,
        colors = ButtonDefaults.buttonColors(
            containerColor = CoreTheme.colors.error,
            contentColor = CoreTheme.colors.onError
        )
    )
}
