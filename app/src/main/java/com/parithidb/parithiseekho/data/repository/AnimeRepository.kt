package com.parithidb.parithiseekho.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.parithidb.parithiseekho.data.api.RetrofitClient
import com.parithidb.parithiseekho.data.api.errorHandling.ApiStatusResponse
import com.parithidb.parithiseekho.data.api.errorHandling.Resource
import com.parithidb.parithiseekho.data.database.AppDatabase
import com.parithidb.parithiseekho.R
import com.parithidb.parithiseekho.data.database.dao.AnimeDao
import com.parithidb.parithiseekho.data.database.entities.AnimeEntity
import com.parithidb.parithiseekho.data.model.GetTopAnimeOutput
import com.parithidb.parithiseekho.util.Common
import com.parithidb.parithiseekho.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import javax.inject.Inject

class AnimeRepository @Inject constructor(
    private val context: Context,
    private val database: AppDatabase,
    private val animeDao: AnimeDao = database.animeDao()
) {

    fun callGetTopAnimeApi(): LiveData<Resource<ApiStatusResponse>> {
        val apiResponse: MutableLiveData<Resource<ApiStatusResponse>> =
            MutableLiveData<Resource<ApiStatusResponse>>()

        if (!Common.isInternetConnected(context)) {
            apiResponse.postValue(
                Resource.error(
                    context.resources.getString(R.string.connection_error_msg),
                    ApiStatusResponse(
                        Constants.STATUS_CODE_CONNECTIVITY_ISSUE,
                        context.resources.getString(R.string.connection_error_msg)
                    )
                )
            )
            return apiResponse
        }

        val animeResponseCall: Call<GetTopAnimeOutput> =
            RetrofitClient.getInstance(context)
                .getAnimeApiService()
                .getTopAnimes()

        animeResponseCall.enqueue(object : Callback<GetTopAnimeOutput> {
            override fun onResponse(
                call: Call<GetTopAnimeOutput>,
                response: Response<GetTopAnimeOutput>
            ) {
                val statusCode = response.code()
                val body = response.body()

                when {
                    statusCode == Constants.STATUS_CODE_RESPONSE_NOT_FOUND -> {
                        apiResponse.postValue(
                            Resource.error(
                                "",
                                ApiStatusResponse(statusCode, "Anime not found")
                            )
                        )
                    }

                    body == null -> {
                        apiResponse.postValue(
                            Resource.error(
                                "",
                                ApiStatusResponse(statusCode, response.message())
                            )
                        )
                    }

                    statusCode == Constants.STATUS_CODE_SUCCESS -> {
                        insertAnimesIntoDb(body)
                        apiResponse.postValue(
                            Resource.success(
                                ApiStatusResponse(
                                    statusCode,
                                    "Success"
                                )
                            )
                        )
                    }

                    else -> {
                        apiResponse.postValue(
                            Resource.error(
                                "Unexpected error",
                                ApiStatusResponse(statusCode, response.message())
                            )
                        )
                    }
                }

            }

            override fun onFailure(p0: Call<GetTopAnimeOutput>, t: Throwable) {
                if (t is RetrofitClient.NoConnectivityException) {
                    // No internet connection
                    apiResponse.postValue(
                        Resource.error(
                            "",
                            ApiStatusResponse(
                                Constants.STATUS_CODE_CONNECTIVITY_ISSUE,
                                context.resources
                                    .getString(R.string.connection_error_msg)
                            )
                        )
                    )
                } else if (t is SocketTimeoutException) {
                    apiResponse.postValue(
                        Resource.error(
                            "",
                            ApiStatusResponse(
                                Constants.STATUS_CODE_TIMEOUT,
                                context.resources
                                    .getString(R.string.connection_timeout_msg)
                            )
                        )
                    )
                } else if (t is IllegalStateException) {
                    apiResponse.postValue(
                        Resource.error(
                            "",
                            ApiStatusResponse(
                                -1,
                                context.resources
                                    .getString(R.string.connection_illegal_state_exception_msg)
                            )
                        )
                    )
                } else {
                    apiResponse.postValue(Resource.error("", ApiStatusResponse(-1, t.message)))
                }
            }

        })
        return apiResponse
    }

    private fun insertAnimesIntoDb(body: GetTopAnimeOutput) {
        CoroutineScope(Dispatchers.IO).launch {
            val animeList = body.animeList.map { anime ->
                AnimeEntity(
                    animeId = anime.animeId,
                    title = anime.title ?: "No title",
                    synopsis = anime.synopsis,
                    episodes = anime.episodes,
                    genres = anime.genres.map { it.name },
                    score = anime.score,
                    rating = anime.rating,
                    imageUrl = anime.images.jpg.imageUrl,
                    trailerUrl = anime.trailer?.embedUrl,
                    producers = anime.producers.map { it.name }
                )

            }
            animeDao.insertAllAnime(animeList)
        }
    }

    fun getAllAnimes() : LiveData<List<AnimeEntity>> {
        return animeDao.getAllAnimes()
    }

    fun getAnimeById(animeId: Int): LiveData<AnimeEntity> {
        return animeDao.getAnimeById(animeId = animeId)
    }
}