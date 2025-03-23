package com.gami13.musicplayer.routes

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.zIndex
import com.gami13.musicplayer.Song
import com.gami13.musicplayer.composables.Container
import com.gami13.musicplayer.composables.Previewer
import com.gami13.musicplayer.composables.SongItem
import com.gami13.musicplayer.mocks.ExampleSuggestions
import com.gami13.musicplayer.mocks.NeverGonnaGiveYouUp
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

@PreviewLightDark
@Preview(wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE)
@Composable
fun DownloadDialogPreview() {
  Previewer {
    DownloadDialog(song = NeverGonnaGiveYouUp, onDismiss = {}, onConfirm = {})
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRoute(modifier: Modifier = Modifier) {
  var searchResults by remember {
    mutableStateOf<List<Song>>(
      listOf(
        NeverGonnaGiveYouUp,
        NeverGonnaGiveYouUp, NeverGonnaGiveYouUp, NeverGonnaGiveYouUp, NeverGonnaGiveYouUp
      )
    )
  }


//  var searchResults by remember { mutableStateOf<List<Song>>(listOf()) }
  val scope = rememberCoroutineScope()
  val textFieldState = rememberTextFieldState()
  var isExpanded by rememberSaveable { mutableStateOf(false) }
  var isSearchLoading by remember { mutableStateOf(false) }
  var searchError by remember { mutableStateOf(false) }
  var suggestions by remember { mutableStateOf(ExampleSuggestions) }
  var songToDownload by remember { mutableStateOf<Song?>(null) }

  fun performSearch(query: String) {
    isExpanded = false
    isSearchLoading = true
    scope.launch {
      try {
        searchError = false
        searchResults = searchYoutube(query.trim())
      } catch (e: Exception) {
        // Add error handling
        searchError = true
        Log.e("SearchRoute", "Error searching YouTube", e)
      } finally {
        isSearchLoading = false
      }
    }
  }


  LaunchedEffect(textFieldState.text) {
    if (textFieldState.text.isEmpty()) {
      suggestions = ExampleSuggestions
      return@LaunchedEffect
    }
    val debounceDelay = 300L

    delay(debounceDelay)

    scope.launch(Dispatchers.IO) {
      try {
        val parsedSuggestions = getAutoCompleteSuggestions(textFieldState.text.toString())
        withContext(Dispatchers.Main) {
          suggestions = parsedSuggestions
        }
      } catch (e: Exception) {
        Log.e("SearchRoute", "Error fetching suggestions", e)
      }
    }
  }

  if (songToDownload != null) {
    DownloadDialog(song = songToDownload!!, onDismiss = { songToDownload = null }) {
      // Download song
      songToDownload = null
    }
  }
  Box(
    modifier
      .fillMaxSize()
      .padding(8.dp),
  ) {
    SearchBar(modifier = Modifier
      .fillMaxWidth()
      .zIndex(10f),
      textFieldState = textFieldState,
      isExpanded = isExpanded,
      onExpandedChange = { isExpanded = it },
      suggestions = suggestions,
      onSearch = {
        performSearch(textFieldState.text.toString().trim())

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

      }, songClicked = {
        songToDownload = it
      }

    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
  modifier: Modifier = Modifier,
  textFieldState: TextFieldState,
  isExpanded: Boolean,
  onExpandedChange: (Boolean) -> Unit,
  suggestions: List<String>,
  onSearch: () -> Unit
) {
  val inputField = @Composable {
    SearchBarDefaults.InputField(
      modifier = Modifier.fillMaxWidth(),
      state = textFieldState,
      onSearch = { onSearch() },
      expanded = isExpanded,
      onExpandedChange = onExpandedChange,
      placeholder = { Text("Hinted search text") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      colors = SearchBarDefaults.inputFieldColors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
          elevation = 1
            .dp
        ),
        focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
          elevation = 1
            .dp
        ),
      )
    )
  }

  DockedSearchBar(
    modifier = modifier,
    inputField = inputField,
    onExpandedChange = onExpandedChange,
    expanded = isExpanded,
    colors = SearchBarDefaults.colors(
      containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
        elevation = 1
          .dp
      ),
      inputFieldColors = SearchBarDefaults.inputFieldColors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
      )
    ),
    content = {
      SearchSuggestions(
        suggestions = suggestions, textFieldState = textFieldState, doSearch = onSearch
      )
    })
}

@Composable
fun SearchResults(
  modifier: Modifier = Modifier,
  searchResults: List<Song>,
  didError: Boolean = false,
  isLoading: Boolean,
  searchQuery: String = "",
  onRetry: () -> Unit = {},
  songClicked: (Song) -> Unit
) {
  Container(modifier) {
    if (isLoading) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    } else if (searchResults.isEmpty()) {
      EmptySearchState(searchQuery, onRetry, didError)
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
      ) {
        itemsIndexed(searchResults) { index, song ->
          SongItem(song, onClick = { songClicked(song) })
          if (index < searchResults.size - 1) {
            HorizontalDivider(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun EmptySearchState(searchQuery: String, onRetry: () -> Unit, isError: Boolean = false) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = Icons.Default.Search,
      contentDescription = null,
      modifier = Modifier
        .size(80.dp)
        .padding(bottom = 16.dp),
      tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    )

    Text(
      text = if (searchQuery.isNotEmpty())
        "No results found for \"$searchQuery\""
      else if (isError)
        "An error occurred when searching YouTube"
      else
        "Search for your favorite songs",
      style = MaterialTheme.typography.bodyLarge,
      textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )

    if (searchQuery.isNotEmpty()) {
      Button(
        onClick = onRetry,
        modifier = Modifier.padding(top = 16.dp)
      ) {
        Text("Try Again")
      }
    }
  }
}


@Composable
fun SearchSuggestions(
  suggestions: List<String>,
  textFieldState: TextFieldState,
  doSearch: () -> Unit
) {
  Column(Modifier.verticalScroll(rememberScrollState())) {
    suggestions.forEach { suggestion ->
      ListItem(
        headlineContent = { Text(suggestion) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        leadingContent = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier
          .clickable {
            textFieldState.setTextAndPlaceCursorAtEnd(suggestion)
            doSearch()
          }
          .fillMaxWidth()
      )
    }
  }
}


@Composable
fun DownloadDialog(
  song: Song,
  onDismiss: () -> Unit,
  onConfirm: (Song) -> Unit
) {
  val dialogWindowProvider = LocalView.current.parent as? DialogWindowProvider

  dialogWindowProvider?.window?.let {
    Log.d("DownloadDialog", "Setting dim amount")
    it.setDimAmount(0.5f)
  }
  var title by remember { mutableStateOf(song.title) }
  var artist by remember { mutableStateOf(song.artist) }
  var album by remember { mutableStateOf(song.album) }

  Dialog(onDismissRequest = onDismiss) {

    Surface(
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surface,
      modifier = Modifier
    ) {
      Column(
        modifier = Modifier
          .padding(24.dp)
          .fillMaxWidth()
      ) {
        Text(
          text = "Download Song",
          style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Editable fields
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Title") },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = artist,
          onValueChange = { artist = it },
          label = { Text("Artist") },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = album,
          onValueChange = { album = it },
          label = { Text("Album") },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismiss) {
            Text("Cancel")
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(onClick = {
            // Create updated song with edited details
            val updatedSong = song.copy(
              title = title,
              artist = artist,
              album = album
            )
            onConfirm(updatedSong)
          }) {
            Text("Download")
          }
        }
      }
    }

  }
}