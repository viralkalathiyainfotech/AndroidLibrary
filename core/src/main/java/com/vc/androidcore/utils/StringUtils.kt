package com.vc.androidcore.utils

import android.util.Patterns
import java.util.Locale

/**
 * Common string manipulation and validation utilities.
 */
object StringUtils {

    /**
     * Safe null or empty checker.
     */
    fun isNullOrEmptySafe(str: CharSequence?): Boolean {
        return str == null || str.trim().isEmpty()
    }

    /**
     * Validates an email address.
     */
    fun isValidEmail(email: CharSequence?): Boolean {
        if (isNullOrEmptySafe(email)) return false
        return Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
    }

    /**
     * Validates a phone number.
     */
    fun isValidPhone(phone: CharSequence?): Boolean {
        if (isNullOrEmptySafe(phone)) return false
        return Patterns.PHONE.matcher(phone!!).matches() && phone.length >= 7
    }

    /**
     * Capitalizes the first character of a string.
     */
    fun capitalizeFirst(str: String?): String {
        if (str.isNullOrEmpty()) return ""
        return str.substring(0, 1).uppercase(Locale.getDefault()) + str.substring(1)
    }

    /**
     * Masks an email (e.g. "johndoe@example.com" -> "j***e@example.com").
     */
    fun maskEmail(email: String?): String {
        if (email.isNullOrBlank() || !email.contains("@")) return email.orEmpty()
        val parts = email.split("@")
        val username = parts[0]
        val domain = parts[1]

        val maskedUser = when {
            username.length <= 2 -> "${username.first()}*"
            else -> "${username.first()}***${username.last()}"
        }
        return "$maskedUser@$domain"
    }

    /**
     * Masks a phone number (e.g. "+1234567890" -> "+12******90").
     */
    fun maskPhone(phone: String?): String {
        if (phone.isNullOrBlank() || phone.length <= 4) return phone.orEmpty()
        val visiblePrefix = phone.take(3)
        val visibleSuffix = phone.takeLast(2)
        val maskCount = phone.length - 5
        return visiblePrefix + "*".repeat(maxOf(1, maskCount)) + visibleSuffix
    }
}

fun CharSequence?.isNullOrEmptySafe(): Boolean = StringUtils.isNullOrEmptySafe(this)
fun CharSequence?.isValidEmail(): Boolean = StringUtils.isValidEmail(this)
fun CharSequence?.isValidPhone(): Boolean = StringUtils.isValidPhone(this)
fun String?.capitalizeFirst(): String = StringUtils.capitalizeFirst(this)
fun String?.maskEmail(): String = StringUtils.maskEmail(this)
fun String?.maskPhone(): String = StringUtils.maskPhone(this)
