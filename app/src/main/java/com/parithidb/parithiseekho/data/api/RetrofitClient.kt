package com.parithidb.parithiseekho.data.api

import android.content.Context
import com.parithidb.parithiseekho.util.Common
import com.parithidb.parithiseekho.util.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException

class RetrofitClient private constructor(context: Context) {
    private val retrofit: Retrofit
    private var githubRepoApiService: AnimeApiService? = null

    companion object {
        @Volatile
        private var retrofitClient: RetrofitClient? = null

        fun getInstance(context: Context): RetrofitClient {
            return retrofitClient ?: synchronized(this) {
                retrofitClient ?: RetrofitClient(context).also { retrofitClient = it }
            }
        }
    }

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        val BASE_URL: String = Constants.ANIME_JIKAN_SERVER
        if (Constants.DEVELOPMENT_MODE) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }


        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ConnectivityInterceptor(context))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getAnimeApiService(): AnimeApiService {
        return githubRepoApiService ?: retrofit.create(AnimeApiService::class.java).also {
            githubRepoApiService = it
        }
    }

    class ConnectivityInterceptor(private val context: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            if (!Common.isInternetConnected(context)) {
                throw NoConnectivityException()
            }

            val builder = chain.request().newBuilder()
            return chain.proceed(builder.build())
        }
    }

    class NoConnectivityException : IOException() {
        override val message: String
            get() = "No connectivity exception"
    }

}