package com.gami13.musicplayer.composables.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gami13.musicplayer.R
import com.gami13.musicplayer.composables.Container
import com.gami13.musicplayer.composables.SearchItem
import com.gami13.musicplayer.utilities.YoutubeSearchResult

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
    when {
      // Show loading indicator when results are being fetched
      isLoading -> LoadingState()
      
      // Show empty state when no results are available
      searchResults.isEmpty() -> EmptySearchState(
        searchQuery = searchQuery,
        onRetry = onRetry, 
        isError = didError
      )
      
      // Show search results list when results are available
      else -> ResultsList(
        searchResults = searchResults,
        onSongClick = songClicked
      )
    }
  }
}

@Composable
private fun LoadingState() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp), 
    contentAlignment = Alignment.Center
  ) {
    CircularProgressIndicator()
  }
}

@Composable
private fun ResultsList(
  searchResults: List<YoutubeSearchResult>,
  onSongClick: (YoutubeSearchResult) -> Unit
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(8.dp)
  ) {
    itemsIndexed(searchResults) { index, song ->
      // Display song item
      SearchItem(song, onClick = { onSongClick(song) })
      
      // Add divider between items, but not after the last item
      if (index < searchResults.size - 1) {
        HorizontalDivider(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        )
      }
    }
  }
}

@Composable
private fun EmptySearchState(
  searchQuery: String, 
  onRetry: () -> Unit, 
  isError: Boolean = false
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Search icon
    Icon(
      imageVector = Icons.Default.Search,
      contentDescription = null,
      modifier = Modifier
        .size(80.dp)
        .padding(bottom = 16.dp),
      tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    )

    // Status message based on current state
    val messageText = when {
      searchQuery.isNotEmpty() -> stringResource(R.string.no_results, searchQuery)
      isError -> stringResource(R.string.youtube_search_error)
      else -> stringResource(R.string.search_description)
    }
    
    Text(
      text = messageText,
      style = MaterialTheme.typography.bodyLarge,
      textAlign = TextAlign.Center
    )

    // Show retry button only when there was a search
    if (searchQuery.isNotEmpty()) {
      Button(
        onClick = onRetry, 
        modifier = Modifier.padding(top = 16.dp)
      ) {
        Text(stringResource(R.string.try_again))
      }
    }
  }
}