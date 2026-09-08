package com.vc.androidcore

import com.vc.androidcore.utils.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateUtilsTest {

    private val zoneId = ZoneId.of("UTC")

    @Test
    fun formatDate_formatsCorrectly() {
        // 2026-09-08 00:00:00 UTC = 1788825600000L
        val millis = 1788825600000L
        val formatted = DateUtils.formatDate(millis, zoneId = zoneId)
        assertEquals("2026-09-08", formatted)
    }

    @Test
    fun isToday_identifiesCurrentTime() {
        val now = System.currentTimeMillis()
        assertTrue(DateUtils.isToday(now))
    }

    @Test
    fun daysBetween_calculatesAccurately() {
        val day1 = 1788825600000L // 2026-09-08
        val day3 = day1 + (2 * 24 * 60 * 60 * 1000L) // 2 days later
        val days = DateUtils.daysBetween(day1, day3, zoneId = zoneId)
        assertEquals(2L, days)
    }
}
