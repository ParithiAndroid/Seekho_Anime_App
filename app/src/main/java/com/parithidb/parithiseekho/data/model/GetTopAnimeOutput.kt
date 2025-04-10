package com.parithidb.parithiseekho.data.model

import com.google.gson.annotations.SerializedName

data class GetTopAnimeOutput(
    @SerializedName("data")
    val animeList: List<AnimeList>
)