package com.serdigital.pataditas.data.local

import androidx.room.TypeConverter

/**
 * Converters para tipos no primitivos en Room.
 */
class Converters {

    @TypeConverter
    fun fromLongList(value: List<Long>): String =
        value.joinToString(",")

    @TypeConverter
    fun toLongList(value: String): List<Long> =
        if (value.isBlank()) emptyList()
        else value.split(",").mapNotNull { it.toLongOrNull() }
}
