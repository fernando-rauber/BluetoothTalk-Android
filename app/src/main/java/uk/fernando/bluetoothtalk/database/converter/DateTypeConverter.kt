package uk.fernando.bluetoothtalk.database.converter

import androidx.room.TypeConverter
import java.util.*

class DateTypeConverter {

    @TypeConverter
    fun serializeDate(date: Date?): Long? = date?.time

    @TypeConverter
    fun deserializeDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }
}