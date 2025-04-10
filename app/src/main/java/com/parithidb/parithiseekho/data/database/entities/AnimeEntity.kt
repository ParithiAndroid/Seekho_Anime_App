package com.parithidb.parithiseekho.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.parithidb.parithiseekho.util.Converters

@Entity(tableName = "ANIME")
@TypeConverters(Converters::class)
class AnimeEntity(
    @PrimaryKey
    @ColumnInfo("animeId")
    val animeId: Int,
    @ColumnInfo("title")
    val title: String?,
    @ColumnInfo("synopsis")
    val synopsis: String?,
    @ColumnInfo("episodes")
    val episodes: Int?,
    @ColumnInfo("genres")
    val genres: List<String>,
    @ColumnInfo("score")
    val score: Float?,
    @ColumnInfo("rating")
    val rating: String?,
    @ColumnInfo("imageUrl")
    val imageUrl: String,
    @ColumnInfo("trailerUrl")
    val trailerUrl: String?,
    @ColumnInfo("producers")
    val producers: List<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnimeEntity

        if (animeId != other.animeId) return false
        if (title != other.title) return false
        if (synopsis != other.synopsis) return false
        if (episodes != other.episodes) return false
        if (genres != other.genres) return false
        if (score != other.score) return false
        if (rating != other.rating) return false
        if (imageUrl != other.imageUrl) return false
        if (trailerUrl != other.trailerUrl) return false
        if (producers != other.producers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = animeId
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (synopsis?.hashCode() ?: 0)
        result = 31 * result + (episodes ?: 0)
        result = 31 * result + genres.hashCode()
        result = 31 * result + (score?.hashCode() ?: 0)
        result = 31 * result + (rating?.hashCode() ?: 0)
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + (trailerUrl?.hashCode() ?: 0)
        result = 31 * result + producers.hashCode()
        return result
    }
}