package com.parithidb.parithiseekho.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.parithidb.parithiseekho.data.database.entities.AnimeEntity

@Dao
interface AnimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAnime(animeEntity: List<AnimeEntity>)

    @Query("SELECT * FROM ANIME ORDER BY score DESC")
    fun getAllAnimes() : LiveData<List<AnimeEntity>>

    @Query("SELECT * FROM ANIME WHERE animeId = :animeId LIMIT 1")
    fun getAnimeById(animeId : Int) : LiveData<AnimeEntity>
}