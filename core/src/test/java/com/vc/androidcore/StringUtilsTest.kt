package com.vc.androidcore

import com.vc.androidcore.utils.StringUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StringUtilsTest {

    @Test
    fun isNullOrEmptySafe_evaluatesCorrectly() {
        assertTrue(StringUtils.isNullOrEmptySafe(null))
        assertTrue(StringUtils.isNullOrEmptySafe(""))
        assertTrue(StringUtils.isNullOrEmptySafe("   "))
        assertFalse(StringUtils.isNullOrEmptySafe("valid"))
    }

    @Test
    fun capitalizeFirst_capitalizesProperly() {
        assertEquals("Hello", StringUtils.capitalizeFirst("hello"))
        assertEquals("A", StringUtils.capitalizeFirst("a"))
        assertEquals("", StringUtils.capitalizeFirst(""))
        assertEquals("", StringUtils.capitalizeFirst(null))
    }

    @Test
    fun maskEmail_masksMiddleCharacters() {
        val masked = StringUtils.maskEmail("john@example.com")
        assertEquals("j***n@example.com", masked)
    }

    @Test
    fun maskPhone_masksMiddleDigits() {
        val masked = StringUtils.maskPhone("1234567890")
        assertEquals("123*****90", masked)
    }
}
