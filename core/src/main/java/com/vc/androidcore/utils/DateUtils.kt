package com.vc.androidcore.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date

/**
 * Modern Date and Time utilities powered by [java.time].
 */
object DateUtils {

    private val DEFAULT_ZONE = ZoneId.systemDefault()

    val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val DISPLAY_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    /**
     * Formats an epoch millisecond timestamp to a date string (e.g. "2026-09-08").
     */
    fun formatDate(
        epochMillis: Long,
        formatter: DateTimeFormatter = DATE_FORMATTER,
        zoneId: ZoneId = DEFAULT_ZONE
    ): String {
        val instant = Instant.ofEpochMilli(epochMillis)
        return instant.atZone(zoneId).toLocalDate().format(formatter)
    }

    /**
     * Formats an epoch millisecond timestamp to a time string (e.g. "09:30:00").
     */
    fun formatTime(
        epochMillis: Long,
        formatter: DateTimeFormatter = TIME_FORMATTER,
        zoneId: ZoneId = DEFAULT_ZONE
    ): String {
        val instant = Instant.ofEpochMilli(epochMillis)
        return instant.atZone(zoneId).toLocalTime().format(formatter)
    }

    /**
     * Formats an epoch millisecond timestamp to a date-time string.
     */
    fun formatDateTime(
        epochMillis: Long,
        formatter: DateTimeFormatter = DATE_TIME_FORMATTER,
        zoneId: ZoneId = DEFAULT_ZONE
    ): String {
        val instant = Instant.ofEpochMilli(epochMillis)
        return instant.atZone(zoneId).toLocalDateTime().format(formatter)
    }

    /**
     * Checks if given timestamp is today.
     */
    fun isToday(epochMillis: Long, zoneId: ZoneId = DEFAULT_ZONE): Boolean {
        val targetDate = Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate()
        val today = LocalDate.now(zoneId)
        return targetDate.isEqual(today)
    }

    /**
     * Checks if given timestamp is yesterday.
     */
    fun isYesterday(epochMillis: Long, zoneId: ZoneId = DEFAULT_ZONE): Boolean {
        val targetDate = Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate()
        val yesterday = LocalDate.now(zoneId).minusDays(1)
        return targetDate.isEqual(yesterday)
    }

    /**
     * Calculates the number of whole days between two timestamps.
     */
    fun daysBetween(startMillis: Long, endMillis: Long, zoneId: ZoneId = DEFAULT_ZONE): Long {
        val startDate = Instant.ofEpochMilli(startMillis).atZone(zoneId).toLocalDate()
        val endDate = Instant.ofEpochMilli(endMillis).atZone(zoneId).toLocalDate()
        return ChronoUnit.DAYS.between(startDate, endDate)
    }

    /**
     * Converts a Java [Date] to epoch milliseconds safely.
     */
    fun toEpochMillis(date: Date?): Long = date?.time ?: 0L

    /**
     * Converts epoch milliseconds to a Java [Date].
     */
    fun toDate(epochMillis: Long): Date = Date(epochMillis)
}
