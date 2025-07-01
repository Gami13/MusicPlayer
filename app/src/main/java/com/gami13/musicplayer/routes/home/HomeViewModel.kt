package com.gami13.musicplayer.routes.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HomeUIState(
  var isLoading: Boolean = true,
  var songs: List<Song> = emptyList(),
)

class HomeViewModel: ViewModel() {

  private val _uiState = MutableStateFlow(HomeUIState())
  val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

  init {
    initializeSettings()
    viewModelScope.launch {
      val result = withContext(Dispatchers.IO) {
        MainActivity.db.songDao().getAll()
      }
      _uiState.value = _uiState.value.copy(
        isLoading = false,
        songs = result,
      )
    }
  }

  private fun initializeSettings() {
    _uiState.value = _uiState.value.copy(
    )
  }
}
