package com.parithidb.parithiseekho.ui.animeDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.parithidb.parithiseekho.data.database.entities.AnimeEntity
import com.parithidb.parithiseekho.data.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnimeDetailsViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val animeId: Int = savedStateHandle["animeId"] ?: -1

    fun getAnimeById(): LiveData<AnimeEntity> {
        return animeRepository.getAnimeById(animeId = animeId)
    }
}