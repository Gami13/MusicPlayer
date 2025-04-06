package com.gami13.musicplayer.routes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.R
import com.gami13.musicplayer.Song
import com.gami13.musicplayer.composables.Container
import com.gami13.musicplayer.composables.LibrarySongItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Preview(showBackground = true)
@Composable
fun HomeRoute(modifier: Modifier = Modifier) {
  var songs by remember { mutableStateOf<List<Song>>(emptyList()) }
  var isLoading by remember { mutableStateOf(true) }

  // Load songs from database when the screen is first shown
  LaunchedEffect(Unit) {
    withContext(Dispatchers.IO) {
      val result = MainActivity.db.songDao().getAll()
      songs = result
      isLoading = false
    }
  }

  RouteWrapper {

    Column(modifier = modifier.fillMaxSize()) {
      // Title section
      Text(
        text = stringResource(R.string.all_songs),
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(16.dp)
      )

      // Songs list
      Container(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) {
        when {
          isLoading -> LoadingState()
          songs.isEmpty() -> EmptyLibraryState()
          else -> SongsList(songs = songs)
        }
      }
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
    androidx.compose.material3.CircularProgressIndicator()
  }
}

@Composable
private fun EmptyLibraryState() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = Icons.Default.MusicNote,
        contentDescription = null,
        modifier = Modifier
          .padding(bottom = 16.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
      )

      Text(
        text = stringResource(R.string.no_songs_in_library),
        style = MaterialTheme.typography.bodyLarge
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = stringResource(R.string.add_songs_from_search),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun SongsList(songs: List<Song>, modifier: Modifier = Modifier) {
  // Create a LazyColumn to display the songs
  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(8.dp)
  ) {
    itemsIndexed(songs) { index, song ->
      // Display song item
      LibrarySongItem(song = song, onClick = {
        MainActivity.musicPlayerState.show()
        MainActivity.musicPlayerState.enqueue(song)
      })

      // Add divider between items, but not after the last item
      if (index < songs.size - 1) {
        HorizontalDivider(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        )
      }
    }
  }
}