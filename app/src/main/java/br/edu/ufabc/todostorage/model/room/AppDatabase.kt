package br.edu.ufabc.todostorage.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TaskRoom::class, Tag::class, TaskTag::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase()
{
    abstract fun taskDao(): TaskDao

    abstract fun tagDao(): TagDao

    abstract fun taskTagDao(): TaskTagDao
}