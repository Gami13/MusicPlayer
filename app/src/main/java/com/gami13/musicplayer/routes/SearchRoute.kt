package com.gami13.musicplayer.routes

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gami13.musicplayer.R
import com.gami13.musicplayer.composables.Container
import com.gami13.musicplayer.composables.DownloadDialog
import com.gami13.musicplayer.composables.Previewer
import com.gami13.musicplayer.composables.SongItem
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRoute(modifier: Modifier = Modifier) {


  var searchResults by remember {
    mutableStateOf<List<YoutubeSearchResult>>(
      listOf(
        NeverGonnaGiveYouUp,
        NeverGonnaGiveYouUp,
        NeverGonnaGiveYouUp,
        NeverGonnaGiveYouUp,
        NeverGonnaGiveYouUp
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
  var songToDownload by remember { mutableStateOf<YoutubeSearchResult?>(null) }

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
    DownloadDialog(song = songToDownload!!, onDismiss = { songToDownload = null })
  }
  Box(
    modifier
      .fillMaxSize()
      .padding(8.dp),
  ) {
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
  val color = MaterialTheme.colorScheme.surfaceColorAtElevation(
    elevation = 1.dp
  )
  val inputField = @Composable {
    SearchBarDefaults.InputField(
      modifier = Modifier.fillMaxWidth(),
      state = textFieldState,
      onSearch = { onSearch() },
      expanded = isExpanded,
      onExpandedChange = onExpandedChange,
      placeholder = { Text(stringResource(R.string.hinted_search_text)) },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      colors = SearchBarDefaults.inputFieldColors(
        unfocusedContainerColor = color,
        focusedContainerColor = color,
      )
    )
  }

  DockedSearchBar(
    modifier = modifier,
    inputField = inputField,
    onExpandedChange = onExpandedChange,
    expanded = isExpanded,
    colors = SearchBarDefaults.colors(
      containerColor = color
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
  searchResults: List<YoutubeSearchResult>,
  didError: Boolean = false,
  isLoading: Boolean,
  searchQuery: String = "",
  onRetry: () -> Unit = {},
  songClicked: (YoutubeSearchResult) -> Unit
) {
  Container(modifier) {
    if (isLoading) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp), contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    } else if (searchResults.isEmpty()) {
      EmptySearchState(searchQuery, onRetry, didError)
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp)
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
      text = if (searchQuery.isNotEmpty()) stringResource(
        R.string.no_results_found_for,
        searchQuery
      )
      else if (isError) stringResource(R.string.an_error_occurred_when_searching_youtube)
      else stringResource(R.string.search_for_your_favorite_songs),
      style = MaterialTheme.typography.bodyLarge,
      textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )

    if (searchQuery.isNotEmpty()) {
      Button(
        onClick = onRetry, modifier = Modifier.padding(top = 16.dp)
      ) {
        Text(stringResource(R.string.try_again))
      }
    }
  }
}


@Composable
fun SearchSuggestions(
  suggestions: List<String>, textFieldState: TextFieldState, doSearch: () -> Unit
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
          .fillMaxWidth())
    }
  }
}

