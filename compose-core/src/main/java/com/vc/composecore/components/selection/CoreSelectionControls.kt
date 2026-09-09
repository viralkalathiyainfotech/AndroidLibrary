package com.vc.composecore.components.selection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import com.vc.composecore.theme.CoreTheme

@Composable
fun CoreCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    label: String? = null,
    subLabel: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    colors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = if (isError) CoreTheme.colors.error else CoreTheme.colors.primary,
        checkmarkColor = CoreTheme.colors.onPrimary
    )
) {
    val rowModifier = if (onCheckedChange != null && enabled) {
        modifier.clickable { onCheckedChange(!checked) }
    } else modifier

    Row(
        modifier = rowModifier.padding(vertical = CoreTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = colors
        )
        if (label != null) {
            Spacer(modifier = Modifier.width(CoreTheme.spacing.xs))
            Column {
                Text(
                    text = label,
                    style = CoreTheme.typography.bodyMedium,
                    color = if (enabled) CoreTheme.colors.onSurface else CoreTheme.colors.outline
                )
                if (subLabel != null) {
                    Text(
                        text = subLabel,
                        style = CoreTheme.typography.caption,
                        color = CoreTheme.colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CoreTriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.padding(vertical = CoreTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TriStateCheckbox(
            state = state,
            onClick = onClick,
            enabled = enabled,
            colors = CheckboxDefaults.colors(checkedColor = CoreTheme.colors.primary)
        )
        if (label != null) {
            Spacer(modifier = Modifier.width(CoreTheme.spacing.xs))
            Text(
                text = label,
                style = CoreTheme.typography.bodyMedium,
                color = if (enabled) CoreTheme.colors.onSurface else CoreTheme.colors.outline
            )
        }
    }
}

@Composable
fun CoreRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    label: String? = null,
    subLabel: String? = null,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(
        selectedColor = CoreTheme.colors.primary
    )
) {
    val rowModifier = if (onClick != null && enabled) {
        modifier.clickable { onClick() }
    } else modifier

    Row(
        modifier = rowModifier.padding(vertical = CoreTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            colors = colors
        )
        if (label != null) {
            Spacer(modifier = Modifier.width(CoreTheme.spacing.xs))
            Column {
                Text(
                    text = label,
                    style = CoreTheme.typography.bodyMedium,
                    color = if (enabled) CoreTheme.colors.onSurface else CoreTheme.colors.outline
                )
                if (subLabel != null) {
                    Text(
                        text = subLabel,
                        style = CoreTheme.typography.caption,
                        color = CoreTheme.colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CoreSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    label: String? = null,
    subLabel: String? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(
        checkedThumbColor = CoreTheme.colors.onPrimary,
        checkedTrackColor = CoreTheme.colors.primary
    )
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = CoreTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (label != null) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = CoreTheme.typography.bodyMedium,
                    color = if (enabled) CoreTheme.colors.onSurface else CoreTheme.colors.outline
                )
                if (subLabel != null) {
                    Text(
                        text = subLabel,
                        style = CoreTheme.typography.caption,
                        color = CoreTheme.colors.onSurfaceVariant
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = colors
        )
    }
}
