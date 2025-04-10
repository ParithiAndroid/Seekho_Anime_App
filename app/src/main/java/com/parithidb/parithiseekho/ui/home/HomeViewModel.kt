package com.parithidb.parithiseekho.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.parithidb.parithiseekho.data.api.errorHandling.ApiStatusResponse
import com.parithidb.parithiseekho.data.api.errorHandling.Resource
import com.parithidb.parithiseekho.data.database.entities.AnimeEntity
import com.parithidb.parithiseekho.data.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun callGetTopAnimeApi(): LiveData<Resource<ApiStatusResponse>> {
        return animeRepository.callGetTopAnimeApi()
    }

    fun getAllAnimes() : LiveData<List<AnimeEntity>> {
        return animeRepository.getAllAnimes()
    }
}