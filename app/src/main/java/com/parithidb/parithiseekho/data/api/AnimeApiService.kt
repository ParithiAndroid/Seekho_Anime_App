package com.parithidb.parithiseekho.data.api

import com.parithidb.parithiseekho.data.model.GetTopAnimeOutput
import retrofit2.Call
import retrofit2.http.GET

interface AnimeApiService {
    @GET("top/anime")
    fun getTopAnimes(): Call<GetTopAnimeOutput>

}