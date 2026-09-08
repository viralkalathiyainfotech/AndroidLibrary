package com.vc.androidcore.database.converters

import androidx.room.TypeConverter
import java.util.Date

/**
 * Room TypeConverters for converting Java [Date] <-> [Long] timestamps.
 */
class DateConverters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
