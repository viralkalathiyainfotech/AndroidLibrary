package com.vc.composecore.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.vc.composecore.resources.CoreText

/**
 * State container for an individual form field.
 */
class CoreFormFieldState<T>(
    initialValue: T,
    val validators: List<Validator<T>> = emptyList(),
    val name: String = ""
) {
    var value: T by mutableStateOf(initialValue)
    var isTouched: Boolean by mutableStateOf(false)
    var isFocused: Boolean by mutableStateOf(false)
    var error: CoreText? by mutableStateOf(null)

    val isValid: Boolean get() = error == null

    fun onValueChange(newValue: T) {
        value = newValue
        if (isTouched) {
            validate()
        }
    }

    fun onFocusChange(focused: Boolean) {
        isFocused = focused
        if (!focused) {
            isTouched = true
            validate()
        }
    }

    fun validate(): Boolean {
        for (validator in validators) {
            when (val result = validator.validate(value)) {
                is ValidationResult.Valid -> continue
                is ValidationResult.Invalid -> {
                    error = result.errorMessage
                    return false
                }
            }
        }
        error = null
        return true
    }

    fun reset(defaultValue: T) {
        value = defaultValue
        isTouched = false
        isFocused = false
        error = null
    }
}

/**
 * State container for an entire form managing multiple fields.
 */
class CoreFormState(
    private val fields: List<CoreFormFieldState<*>>
) {
    val isValid: Boolean get() = fields.all { it.isValid }
    val isTouched: Boolean get() = fields.any { it.isTouched }

    fun validateAll(): Boolean {
        var allValid = true
        for (field in fields) {
            field.isTouched = true
            if (!field.validate()) {
                allValid = false
            }
        }
        return allValid
    }

    fun resetAll() {
        // Individual field states are reset with their respective defaults
    }
}

@Composable
fun <T> rememberFormFieldState(
    initialValue: T,
    validators: List<Validator<T>> = emptyList(),
    name: String = ""
): CoreFormFieldState<T> {
    return remember { CoreFormFieldState(initialValue, validators, name) }
}

@Composable
fun rememberFormState(vararg fields: CoreFormFieldState<*>): CoreFormState {
    return remember(fields) { CoreFormState(fields.toList()) }
}
