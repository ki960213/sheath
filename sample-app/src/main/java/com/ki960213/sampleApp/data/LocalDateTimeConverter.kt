package com.ki960213.sampleApp.data

import androidx.room.TypeConverter
import java.time.LocalDateTime

class LocalDateTimeConverter {
    @TypeConverter
    fun convertToDate(dateString: String): LocalDateTime = LocalDateTime.parse(dateString)

    @TypeConverter
    fun convertToDateString(date: LocalDateTime): String = date.toString()
}
