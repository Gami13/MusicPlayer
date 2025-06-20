package com.gami13.musicplayer.routes

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.SongDownloader
import com.gami13.musicplayer.composables.DownloadDialog
import com.gami13.musicplayer.composables.Previewer
import com.gami13.musicplayer.composables.search.SearchBar
import com.gami13.musicplayer.composables.search.SearchResults
import com.gami13.musicplayer.mocks.ExampleSuggestions
import com.gami13.musicplayer.mocks.NeverGonnaGiveYouUp
import com.gami13.musicplayer.utilities.YoutubeSearchResult
import com.gami13.musicplayer.utilities.getAutoCompleteSuggestions
import com.gami13.musicplayer.utilities.searchYoutube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@PreviewLightDark
@Preview(wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE)
@Composable
fun SearchRoutePreview() {
  Previewer {
    SearchRoute()
  }
}

private const val isMock = true

private val mockSearchResults by lazy {
  listOf(
    NeverGonnaGiveYouUp,
    NeverGonnaGiveYouUp,
    NeverGonnaGiveYouUp,
    NeverGonnaGiveYouUp,
    NeverGonnaGiveYouUp
  )
}

@Composable
fun SearchRoute(modifier: Modifier = Modifier) {
  var searchResults by remember { mutableStateOf<List<YoutubeSearchResult>>(emptyList()) }
  var isSearchLoading by remember { mutableStateOf(isMock) }

  val scope = rememberCoroutineScope()
  val textFieldState = rememberTextFieldState()
  var isExpanded by rememberSaveable { mutableStateOf(false) }
  var searchError by remember { mutableStateOf(false) }
  var suggestions by remember { mutableStateOf(ExampleSuggestions) }
  var songToDownload by remember { mutableStateOf<YoutubeSearchResult?>(null) }
  var songDownloader: SongDownloader? by remember { mutableStateOf(null) }
  var downloadProgress by remember { mutableFloatStateOf(0f) }
  var downloadEta by remember { mutableLongStateOf(0L) }

  LaunchedEffect(Unit) {
    if (isMock) {
      delay(100)
      searchResults = mockSearchResults
      isSearchLoading = false
    }
  }

  fun performSearch(query: String) {
    isExpanded = false
    isSearchLoading = true
    scope.launch {
      try {
        searchError = false
        searchResults = searchYoutube(query.trim())
      } catch (e: Exception) {
        searchError = true
        Log.e("SearchRoute", "Error searching YouTube", e)
      } finally {
        isSearchLoading = false
      }
    }
  }

  var userInput by remember { mutableStateOf("") }
  LaunchedEffect(textFieldState.text) {
    userInput = textFieldState.text.toString()
  }

  LaunchedEffect(userInput) {
    if (userInput.isEmpty()) {
      suggestions = ExampleSuggestions
      return@LaunchedEffect
    }
    val debounceDelay = 200L

    delay(debounceDelay)

    scope.launch(Dispatchers.IO) {
      try {
        val parsedSuggestions = getAutoCompleteSuggestions(userInput)
        withContext(Dispatchers.Main) {
          suggestions = parsedSuggestions
        }
      } catch (e: Exception) {
        Log.e("SearchRoute", "Error fetching suggestions", e)
      }
    }
  }

  if (songToDownload != null) {
    DownloadDialog(
      song = songToDownload!!,
      onDismiss = {
        songToDownload = null
        if (downloadProgress != 100.0f) {
          songDownloader!!.cancelDownload()

        }
        Log.d("SearchRoute", "Download cancelled")
        songDownloader = null
      },
      onSave = { song ->
        Log.d("SearchRoute", "Saving song: $song")
        songDownloader!!.saveSong(song)
      },
      getProgress = {
        downloadProgress
      },
      getEta = {
        downloadEta
      },
    )
  }
  RouteWrapper {
    SearchBar(
      modifier = Modifier
        .fillMaxWidth()
        .zIndex(10f),
      textFieldState = textFieldState,
      isExpanded = isExpanded,
      onExpandedChange = { isExpanded = it },
      suggestions = suggestions,
      onSearch = {
        performSearch(textFieldState.text.toString().trim())
      },
      cancelSearch = {
        textFieldState.setTextAndPlaceCursorAtEnd("")
        isSearchLoading = false
        isExpanded = false
      })

    SearchResults(
      searchResults = searchResults,
      didError = searchError,
      isLoading = isSearchLoading,
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 64.dp),
      searchQuery = textFieldState.text.toString(),
      onRetry = {
        performSearch(textFieldState.text.toString().trim())
      },
      songClicked = {
        songDownloader = SongDownloader(
          songUrl = it.videoUrl,
          onComplete = {
            Log.d("SongDownloader", "Download complete: $it")
            songToDownload = null
            songDownloader = null
            scope.launch {

              MainActivity.snackbarHostState.showSnackbar(
                message = "Download complete", duration =
                  SnackbarDuration.Short, withDismissAction = true
              )
            }

          },
          onProgress = { progress, eta, message ->
            downloadProgress = progress
            downloadEta = eta
            Log.d("SongDownloader", "Progress: $progress, ETA: $eta, Message: $message")
          },
          onError = { error ->
            Log.e("SongDownloader", "Error: $error")
          },


          )
        Log.d("SearchRoute", "Song clicked: ${it.title}")
        scope.launch {
          songDownloader!!.startDownload()
        }
        songToDownload = it

      }
    )
  }
}