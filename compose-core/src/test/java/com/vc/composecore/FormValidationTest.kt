package com.vc.composecore

import com.vc.composecore.forms.CoreFormFieldState
import com.vc.composecore.forms.CoreFormState
import com.vc.composecore.forms.EmailValidator
import com.vc.composecore.forms.MaxLengthValidator
import com.vc.composecore.forms.MinLengthValidator
import com.vc.composecore.forms.NumberValidator
import com.vc.composecore.forms.PhoneValidator
import com.vc.composecore.forms.RegexValidator
import com.vc.composecore.forms.RequiredValidator
import com.vc.composecore.forms.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FormValidationTest {

    @Test
    fun `required validator should fail for blank string and pass for non-blank`() {
        val validator = RequiredValidator()

        assertTrue(validator.validate("Hello").isValid)
        assertFalse(validator.validate("").isValid)
        assertFalse(validator.validate("   ").isValid)
        assertFalse(validator.validate(null).isValid)
    }

    @Test
    fun `email validator should validate emails correctly`() {
        val validator = EmailValidator()

        assertTrue(validator.validate("test@example.com").isValid)
        assertTrue(validator.validate("user.name+tag@sub.domain.org").isValid)
        assertTrue(validator.validate("").isValid) // blank passes if not marked required
        assertFalse(validator.validate("plainaddress").isValid)
        assertFalse(validator.validate("@missingusername.com").isValid)
        assertFalse(validator.validate("username@.com").isValid)
    }

    @Test
    fun `phone validator should validate international numbers`() {
        val validator = PhoneValidator()

        assertTrue(validator.validate("+12345678901").isValid)
        assertTrue(validator.validate("9876543210").isValid)
        assertTrue(validator.validate("").isValid)
        assertFalse(validator.validate("123").isValid)
        assertFalse(validator.validate("abc1234567").isValid)
    }

    @Test
    fun `min length validator should validate threshold`() {
        val validator = MinLengthValidator(minLength = 6)

        assertTrue(validator.validate("123456").isValid)
        assertTrue(validator.validate("1234567").isValid)
        assertFalse(validator.validate("12345").isValid)
    }

    @Test
    fun `max length validator should validate threshold`() {
        val validator = MaxLengthValidator(maxLength = 5)

        assertTrue(validator.validate("12345").isValid)
        assertTrue(validator.validate("").isValid)
        assertFalse(validator.validate("123456").isValid)
    }

    @Test
    fun `number validator should validate range`() {
        val validator = NumberValidator(min = 10.0, max = 100.0)

        assertTrue(validator.validate("50").isValid)
        assertTrue(validator.validate("10.0").isValid)
        assertTrue(validator.validate("100").isValid)
        assertFalse(validator.validate("5").isValid)
        assertFalse(validator.validate("150").isValid)
        assertFalse(validator.validate("abc").isValid)
    }

    @Test
    fun `form field state validates touched and value changes`() {
        val field = CoreFormFieldState(
            initialValue = "",
            validators = listOf(RequiredValidator(), MinLengthValidator(3))
        )

        // Initial state
        assertTrue(field.isValid)
        assertFalse(field.isTouched)

        // Touched with invalid value
        field.onFocusChange(false)
        assertTrue(field.isTouched)
        assertFalse(field.isValid)

        // Valid update
        field.onValueChange("Valid")
        assertTrue(field.isValid)
    }

    @Test
    fun `core form state validates all fields together`() {
        val emailField = CoreFormFieldState("", listOf(RequiredValidator(), EmailValidator()))
        val passwordField = CoreFormFieldState("", listOf(RequiredValidator(), MinLengthValidator(6)))

        val form = CoreFormState(listOf(emailField, passwordField))

        assertFalse(form.validateAll())
        assertFalse(emailField.isValid)
        assertFalse(passwordField.isValid)

        emailField.value = "user@example.com"
        emailField.validate()
        passwordField.value = "password123"
        passwordField.validate()

        assertTrue(form.validateAll())
        assertTrue(form.isValid)
    }
}
