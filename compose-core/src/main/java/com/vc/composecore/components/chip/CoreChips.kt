package com.vc.composecore.components.chip

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreTheme

@Composable
fun CoreChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    CoreAssistChip(
        label = label,
        onClick = onClick,
        modifier = modifier,
        leadingIcon = leadingIcon,
        enabled = enabled
    )
}

@Composable
fun CoreAssistChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label, style = CoreTheme.typography.labelMedium) },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null, modifier = Modifier.size(18.dp)) }
        },
        shape = RoundedCornerShape(CoreRadius.small),
        colors = AssistChipDefaults.assistChipColors(
            labelColor = CoreTheme.colors.onSurface
        )
    )
}

@Composable
fun CoreInputChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    avatar: (@Composable () -> Unit)? = null,
    enabled: Boolean = true
) {
    InputChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = CoreTheme.typography.labelMedium) },
        modifier = modifier,
        enabled = enabled,
        avatar = avatar,
        trailingIcon = {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove chip",
                    modifier = Modifier.size(14.dp)
                )
            }
        },
        shape = RoundedCornerShape(CoreRadius.small),
        colors = InputChipDefaults.inputChipColors(
            selectedContainerColor = CoreTheme.colors.primaryContainer,
            selectedLabelColor = CoreTheme.colors.onPrimaryContainer
        )
    )
}

@Composable
fun CoreSuggestionChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    SuggestionChip(
        onClick = onClick,
        label = { Text(label, style = CoreTheme.typography.labelMedium) },
        modifier = modifier,
        enabled = enabled,
        icon = icon?.let {
            { Icon(imageVector = it, contentDescription = null, modifier = Modifier.size(16.dp)) }
        },
        shape = RoundedCornerShape(CoreRadius.small)
    )
}

@Composable
fun CoreFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = CoreTheme.typography.labelMedium) },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null, modifier = Modifier.size(18.dp)) }
        },
        shape = RoundedCornerShape(CoreRadius.small),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CoreTheme.colors.primaryContainer,
            selectedLabelColor = CoreTheme.colors.onPrimaryContainer
        )
    )
}
