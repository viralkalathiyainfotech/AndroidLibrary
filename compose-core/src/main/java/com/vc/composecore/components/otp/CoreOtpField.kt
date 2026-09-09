package com.vc.composecore.components.otp

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vc.composecore.theme.CoreRadius
import com.vc.composecore.theme.CoreTheme

/**
 * Enterprise OTP / PIN field supporting configurable length, alphanumeric or numeric input,
 * auto-focus, paste, backspace, masking, error states, and completion callbacks.
 */
@Composable
fun CoreOtpField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    isMasked: Boolean = false,
    isNumericOnly: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    boxSize: Dp = 48.dp,
    autoFocus: Boolean = false,
    onComplete: ((String) -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (autoFocus) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(otpValue) {
        if (otpValue.length == length) {
            onComplete?.invoke(otpValue)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = otpValue,
            onValueChange = { newValue ->
                val filtered = if (isNumericOnly) newValue.filter { it.isDigit() } else newValue
                if (filtered.length <= length) {
                    onOtpChange(filtered)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isNumericOnly) KeyboardType.NumberPassword else KeyboardType.Ascii,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (otpValue.length == length) onComplete?.invoke(otpValue)
                }
            ),
            modifier = Modifier.focusRequester(focusRequester),
            decorationBox = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(CoreTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until length) {
                        val char = otpValue.getOrNull(i)
                        val isFocused = otpValue.length == i
                        val borderColor = when {
                            isError -> CoreTheme.colors.error
                            isFocused -> CoreTheme.colors.primary
                            char != null -> CoreTheme.colors.primary.copy(alpha = 0.6f)
                            else -> CoreTheme.colors.outlineVariant
                        }

                        val displayText = when {
                            char == null -> ""
                            isMasked -> "•"
                            else -> char.toString()
                        }

                        Box(
                            modifier = Modifier
                                .size(boxSize)
                                .border(
                                    width = if (isFocused || isError) 2.dp else 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(CoreRadius.small)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayText,
                                style = CoreTheme.typography.titleLarge,
                                color = if (isError) CoreTheme.colors.error else CoreTheme.colors.onBackground,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(CoreTheme.spacing.xs))
            Text(
                text = errorMessage,
                style = CoreTheme.typography.error,
                color = CoreTheme.colors.error,
                textAlign = TextAlign.Center
            )
        }
    }
}
