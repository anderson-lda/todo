package br.edu.ufabc.todostorage.model.room

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time

    @TypeConverter
    fun timestampToDate(timestamp: Long): Date = Date(timestamp)
}