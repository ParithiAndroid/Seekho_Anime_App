package com.parithidb.parithiseekho.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.parithidb.parithiseekho.data.database.dao.AnimeDao
import com.parithidb.parithiseekho.data.database.entities.AnimeEntity

@Database(entities = [AnimeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animeDao() : AnimeDao

    fun clearDatabase() {
        clearAllTables()
    }
}