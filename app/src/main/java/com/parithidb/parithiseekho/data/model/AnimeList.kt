package com.parithidb.parithiseekho.data.model

import com.google.gson.annotations.SerializedName

data class AnimeList(
    @SerializedName("mal_id")
    val animeId : Int,
    @SerializedName("title_english")
    val title: String?,
    @SerializedName("images")
    val images: Images,
    @SerializedName("trailer")
    val trailer: Trailer?,
    @SerializedName("synopsis")
    val synopsis: String?,
    @SerializedName("genres")
    val genres: List<Name>,
    @SerializedName("producers")
    val producers: List<Name>,
    @SerializedName("episodes")
    val episodes: Int?,
    @SerializedName("score")
    val score: Float?,
    @SerializedName("rating")
    val rating : String?
)
