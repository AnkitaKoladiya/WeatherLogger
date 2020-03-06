package com.weather.logger.database.converter


import androidx.room.TypeConverter

import java.util.Date

/**
 * @author Enlistech.
 */

class DateConverter {

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}
