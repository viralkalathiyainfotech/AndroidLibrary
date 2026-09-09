package com.vc.composecore.forms

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.vc.composecore.resources.CoreText

@Immutable
sealed interface ValidationResult {
    data object Valid : ValidationResult

    data class Invalid(
        val errorMessage: CoreText
    ) : ValidationResult {
        constructor(message: String) : this(CoreText.Dynamic(message))
        constructor(@StringRes resId: Int, vararg args: Any) : this(CoreText.Resource(resId, args.toList()))
    }

    val isValid: Boolean get() = this is Valid
}

/**
 * Functional interface for form validation rules.
 */
fun interface Validator<in T> {
    fun validate(value: T): ValidationResult
}

class RequiredValidator(
    private val errorMessage: CoreText = CoreText.Dynamic("This field is required")
) : Validator<Any?> {
    override fun validate(value: Any?): ValidationResult {
        val isValid = when (value) {
            null -> false
            is CharSequence -> value.isNotBlank()
            is Collection<*> -> value.isNotEmpty()
            else -> true
        }
        return if (isValid) ValidationResult.Valid else ValidationResult.Invalid(errorMessage)
    }
}

class EmailValidator(
    private val errorMessage: CoreText = CoreText.Dynamic("Invalid email address format")
) : Validator<String> {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")

    override fun validate(value: String): ValidationResult {
        if (value.isBlank()) return ValidationResult.Valid
        return if (emailRegex.matches(value.trim())) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errorMessage)
        }
    }
}

class PhoneValidator(
    private val errorMessage: CoreText = CoreText.Dynamic("Invalid phone number format")
) : Validator<String> {
    private val phoneRegex = Regex("^[+]?[0-9]{8,15}\$")

    override fun validate(value: String): ValidationResult {
        if (value.isBlank()) return ValidationResult.Valid
        return if (phoneRegex.matches(value.trim())) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errorMessage)
        }
    }
}

class MinLengthValidator(
    private val minLength: Int,
    private val errorMessage: CoreText = CoreText.Dynamic("Must be at least $minLength characters")
) : Validator<String> {
    override fun validate(value: String): ValidationResult {
        return if (value.length >= minLength) ValidationResult.Valid else ValidationResult.Invalid(errorMessage)
    }
}

class MaxLengthValidator(
    private val maxLength: Int,
    private val errorMessage: CoreText = CoreText.Dynamic("Must not exceed $maxLength characters")
) : Validator<String> {
    override fun validate(value: String): ValidationResult {
        return if (value.length <= maxLength) ValidationResult.Valid else ValidationResult.Invalid(errorMessage)
    }
}

class RegexValidator(
    private val regex: Regex,
    private val errorMessage: CoreText = CoreText.Dynamic("Invalid input format")
) : Validator<String> {
    override fun validate(value: String): ValidationResult {
        return if (regex.matches(value)) ValidationResult.Valid else ValidationResult.Invalid(errorMessage)
    }
}

class NumberValidator(
    private val min: Double = Double.MIN_VALUE,
    private val max: Double = Double.MAX_VALUE,
    private val errorMessage: CoreText = CoreText.Dynamic("Value must be a valid number between $min and $max")
) : Validator<String> {
    override fun validate(value: String): ValidationResult {
        if (value.isBlank()) return ValidationResult.Valid
        val parsed = value.toDoubleOrNull()
        return if (parsed != null && parsed in min..max) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errorMessage)
        }
    }
}

class CustomValidator<T>(
    private val predicate: (T) -> Boolean,
    private val errorMessage: CoreText
) : Validator<T> {
    override fun validate(value: T): ValidationResult {
        return if (predicate(value)) ValidationResult.Valid else ValidationResult.Invalid(errorMessage)
    }
}
