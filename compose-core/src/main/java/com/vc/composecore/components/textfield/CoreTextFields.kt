package com.vc.composecore.components.textfield

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreTheme

@Composable
fun CoreTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    error: String? = null,
    helperText: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = error != null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    maxLength: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            label = label?.let { { Text(it, style = CoreTheme.typography.bodySmall) } },
            placeholder = placeholder?.let { { Text(it, style = CoreTheme.typography.bodyMedium, color = CoreTheme.colors.outline) } },
            leadingIcon = leadingIcon?.let { { Icon(imageVector = it, contentDescription = null) } },
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            shape = RoundedCornerShape(CoreRadius.small),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CoreTheme.colors.surfaceVariant.copy(alpha = 0.35f),
                unfocusedContainerColor = CoreTheme.colors.surfaceVariant.copy(alpha = 0.2f),
                disabledContainerColor = CoreTheme.colors.surfaceVariant.copy(alpha = 0.1f),
                focusedIndicatorColor = CoreTheme.colors.primary,
                errorIndicatorColor = CoreTheme.colors.error
            )
        )
        if (error != null) {
            Text(
                text = error,
                style = CoreTheme.typography.error,
                color = CoreTheme.colors.error,
                modifier = Modifier.padding(start = CoreTheme.spacing.md, top = CoreTheme.spacing.xxs)
            )
        } else if (helperText != null) {
            Text(
                text = helperText,
                style = CoreTheme.typography.caption,
                color = CoreTheme.colors.onSurfaceVariant,
                modifier = Modifier.padding(start = CoreTheme.spacing.md, top = CoreTheme.spacing.xxs)
            )
        }
    }
}

@Composable
fun CoreOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    error: String? = null,
    helperText: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = error != null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    maxLength: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            label = label?.let { { Text(it, style = CoreTheme.typography.bodySmall) } },
            placeholder = placeholder?.let { { Text(it, style = CoreTheme.typography.bodyMedium, color = CoreTheme.colors.outline) } },
            leadingIcon = leadingIcon?.let { { Icon(imageVector = it, contentDescription = null) } },
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            shape = RoundedCornerShape(CoreRadius.small),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CoreTheme.colors.primary,
                unfocusedBorderColor = CoreTheme.colors.outlineVariant,
                errorBorderColor = CoreTheme.colors.error
            )
        )
        if (error != null) {
            Text(
                text = error,
                style = CoreTheme.typography.error,
                color = CoreTheme.colors.error,
                modifier = Modifier.padding(start = CoreTheme.spacing.md, top = CoreTheme.spacing.xxs)
            )
        } else if (helperText != null) {
            Text(
                text = helperText,
                style = CoreTheme.typography.caption,
                color = CoreTheme.colors.onSurfaceVariant,
                modifier = Modifier.padding(start = CoreTheme.spacing.md, top = CoreTheme.spacing.xxs)
            )
        }
    }
}

@Composable
fun CorePasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    placeholder: String? = null,
    error: String? = null,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }

    CoreOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        error = error,
        enabled = enabled,
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = KeyboardActions(onAny = { onImeAction() }),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    tint = CoreTheme.colors.outline
                )
            }
        }
    )
}

@Composable
fun CoreSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    onSearch: (String) -> Unit = {},
    onClear: () -> Unit = {}
) {
    CoreOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = Icons.Default.Search,
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = {
                    onValueChange("")
                    onClear()
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = CoreTheme.colors.outline
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(value) })
    )
}

@Composable
fun CoreEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Email Address",
    error: String? = null,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next
) {
    CoreOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        error = error,
        enabled = enabled,
        leadingIcon = Icons.Default.Email,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = imeAction),
        singleLine = true
    )
}

@Composable
fun CorePhoneField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Phone Number",
    error: String? = null,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next
) {
    CoreOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        error = error,
        enabled = enabled,
        leadingIcon = Icons.Default.Phone,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = imeAction),
        singleLine = true
    )
}

@Composable
fun CoreNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    error: String? = null,
    isDecimal: Boolean = false,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Done
) {
    CoreOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        error = error,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number,
            imeAction = imeAction
        ),
        singleLine = true
    )
}

@Composable
fun CoreDropdownField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "Select an option",
    error: String? = null,
    enabled: Boolean = true
) {
    CoreOutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = modifier.clickable(enabled = enabled, onClick = onClick),
        label = label,
        placeholder = placeholder,
        error = error,
        readOnly = true,
        enabled = enabled,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown selector",
                tint = CoreTheme.colors.onSurfaceVariant
            )
        }
    )
}

@Composable
fun CoreDateField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Date",
    placeholder: String = "Select date",
    error: String? = null,
    enabled: Boolean = true
) {
    CoreOutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = modifier.clickable(enabled = enabled, onClick = onClick),
        label = label,
        placeholder = placeholder,
        error = error,
        readOnly = true,
        enabled = enabled,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Pick date",
                tint = CoreTheme.colors.onSurfaceVariant
            )
        }
    )
}

/**
 * Modern, simple text field featuring an external top label, clear placeholder,
 * unbroken rounded outline border, and optional prefix/suffix icons.
 * Matches clean modern UI designs where the label sits outside and above the input box.
 */
@Composable
fun CoreSimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    error: String? = null,
    helperText: String? = null,
    isRequired: Boolean = false,
    leadingIcon: ImageVector? = null,
    leadingIconContent: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    maxLength: Int = Int.MAX_VALUE,
    shape: Shape = RoundedCornerShape(CoreRadius.small),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val actualLeadingIcon: (@Composable () -> Unit)? = leadingIconContent ?: leadingIcon?.let {
        { Icon(imageVector = it, contentDescription = null) }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = CoreTheme.spacing.xs)
            ) {
                Text(
                    text = label,
                    style = CoreTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = CoreTheme.colors.onSurface
                )
                if (isRequired) {
                    Text(
                        text = " *",
                        style = CoreTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = CoreTheme.colors.error
                    )
                }
            }
        }
        OutlinedTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            placeholder = placeholder?.let {
                {
                    Text(
                        text = it,
                        style = CoreTheme.typography.bodyMedium,
                        color = CoreTheme.colors.outline
                    )
                }
            },
            leadingIcon = actualLeadingIcon,
            trailingIcon = trailingIcon,
            isError = error != null,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            shape = shape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CoreTheme.colors.surface,
                unfocusedContainerColor = CoreTheme.colors.surface,
                disabledContainerColor = CoreTheme.colors.surfaceVariant.copy(alpha = 0.2f),
                focusedBorderColor = CoreTheme.colors.primary,
                unfocusedBorderColor = CoreTheme.colors.outlineVariant,
                errorBorderColor = CoreTheme.colors.error,
                focusedTextColor = CoreTheme.colors.onSurface,
                unfocusedTextColor = CoreTheme.colors.onSurface,
                cursorColor = CoreTheme.colors.primary
            )
        )
        if (error != null) {
            Text(
                text = error,
                style = CoreTheme.typography.error,
                color = CoreTheme.colors.error,
                modifier = Modifier.padding(start = CoreTheme.spacing.xs, top = CoreTheme.spacing.xxs)
            )
        } else if (helperText != null) {
            Text(
                text = helperText,
                style = CoreTheme.typography.caption,
                color = CoreTheme.colors.onSurfaceVariant,
                modifier = Modifier.padding(start = CoreTheme.spacing.xs, top = CoreTheme.spacing.xxs)
            )
        }
    }
}

/**
 * Clean, simple Email text field with top label and placeholder.
 */
@Composable
fun CoreSimpleEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = "Email Address",
    placeholder: String? = "Enter your email",
    error: String? = null,
    helperText: String? = null,
    isRequired: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    imeAction: ImeAction = ImeAction.Next
) {
    CoreSimpleTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        error = error,
        helperText = helperText,
        isRequired = isRequired,
        enabled = enabled,
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = imeAction),
        singleLine = true
    )
}

/**
 * Clean, simple Password text field with top label, placeholder, and visibility toggle.
 */
@Composable
fun CoreSimplePasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = "Password",
    placeholder: String? = "Enter your password",
    error: String? = null,
    helperText: String? = null,
    isRequired: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }

    CoreSimpleTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        error = error,
        helperText = helperText,
        isRequired = isRequired,
        enabled = enabled,
        leadingIcon = leadingIcon,
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = KeyboardActions(onAny = { onImeAction() }),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    tint = CoreTheme.colors.outline
                )
            }
        }
    )
}

/**
 * Clean, simple Search text field with external top label and clear button.
 */
@Composable
fun CoreSimpleSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "Search...",
    onSearch: (String) -> Unit = {},
    onClear: () -> Unit = {}
) {
    CoreSimpleTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        leadingIcon = Icons.Default.Search,
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = {
                    onValueChange("")
                    onClear()
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = CoreTheme.colors.outline
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(value) })
    )
}

