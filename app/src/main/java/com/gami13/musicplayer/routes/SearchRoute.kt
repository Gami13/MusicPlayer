package com.gami13.musicplayer.routes

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.gami13.musicplayer.Container
import com.gami13.musicplayer.Song
import com.gami13.musicplayer.utilities.autoCompleteSearch
import com.gami13.musicplayer.utilities.formatDuration
import com.gami13.musicplayer.utilities.formatTimeAgo
import com.gami13.musicplayer.utilities.getThumbnailImageBitmap
import com.gami13.musicplayer.utilities.parseYouTubeSuggestions
import com.gami13.musicplayer.utilities.searchYoutube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import java.io.ByteArrayOutputStream

fun createBlue(): ByteArray {

  val bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
  bmp.eraseColor(android.graphics.Color.BLUE)
  val outputStream = ByteArrayOutputStream()
  bmp.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, outputStream)
  return outputStream.toByteArray()


}

val RickAstley = Song(
  youtubeId = "dQw4w9WgXcQ",
  title = "Never Gonna Give You Up",
  artist = "Rick Astley",
  album = "Whenever You Need Somebody",
  genre = "Pop",
  year = 1987,
  storagePath = "",
  duration = 213,
  //blue
  cover = createBlue(),
  isFavorite = false,
  publishedAt = ("2022-01-01T00:00:00")
)
val ExampleSuggestions = listOf(
  "Despacito",
  "Lose Yourself",
  "Bohemian Rhapsody",
  "Hotel California",
  "Stairway to Heaven",
  "All I Want for Christmas Is You",
  "Somebody Once Told Me"
)

@Composable
fun SongItem(song: Song) {
  ListItem(
    headlineContent = {
      SongTitle(title = song.title)
    },
    supportingContent = {
      SongMetadata(song = song)
    },
    leadingContent = {
      SongThumbnail(song = song)
    },
    trailingContent = {
      DownloadButton()
    },
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = { })
      .padding(vertical = 2.dp), // Reduced vertical padding
    colors = ListItemDefaults.colors(
      containerColor = Color.Transparent
    )
  )
}

@Composable
fun SongTitle(title: String) {
  Text(
    text = title,
    fontWeight = FontWeight.Bold,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
    fontSize = 14.sp // Reduced font size
  )
}

@Composable
fun SongMetadata(song: Song) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(4.dp), // Reduced spacing
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(top = 2.dp) // Reduced top padding
  ) {
    Text(
      text = song.artist, fontSize = 11.sp, // Reduced font size
      maxLines = 1, overflow = TextOverflow.Ellipsis
    )

    if (song.duration > 0) {
      MetadataSeparator()

      Text(
        text = song.duration.formatDuration(), fontSize = 11.sp, // Reduced font size
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    MetadataSeparator()

    Text(
      text = LocalDateTime.parse(song.publishedAt).formatTimeAgo(),
      fontSize = 11.sp, // Reduced font size
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
fun MetadataSeparator() {
  Text(
    text = "•", fontSize = 11.sp, // Reduced font size
    color = MaterialTheme.colorScheme.onSurfaceVariant
  )
}

@Composable
fun SongThumbnail(song: Song) {
  Box(
    modifier = Modifier.heightIn(min = 56.dp) // Reduced height
  ) {
    Image(
      bitmap = song.getThumbnailImageBitmap(),
      contentDescription = "Thumbnail for ${song.title}",
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .size(56.dp, 56.dp) // Reduced size
        .clip(RoundedCornerShape(6.dp)) // Smaller corner radius
    )
  }
}

@Composable
fun DownloadButton() {
  IconButton(
    onClick = {}, modifier = Modifier.size(36.dp) // Smaller icon button
  ) {
    Icon(
      imageVector = Icons.Default.Download,
      contentDescription = "Download song",
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(20.dp) // Smaller icon
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SearchRoute(modifier: Modifier = Modifier) {
  var searchResults by remember { mutableStateOf<List<Song>>(listOf(RickAstley)) }
  val scope = rememberCoroutineScope()
  val textFieldState = rememberTextFieldState()
  var isExpanded by rememberSaveable { mutableStateOf(false) }
  var isSearchLoading by remember { mutableStateOf(false) }

  val suggestions = remember { mutableStateOf(ExampleSuggestions) }

  LaunchedEffect(textFieldState.text) {
    if (textFieldState.text.isNotEmpty()) {
      delay(300)
      scope.launch(Dispatchers.IO) {
        try {
          val result = autoCompleteSearch(textFieldState.text.toString())
          val parsedSuggestions = parseYouTubeSuggestions(result)
          withContext(Dispatchers.Main) {
            suggestions.value = parsedSuggestions
          }
        } catch (e: Exception) {
          Log.e("SearchRoute", "Error fetching suggestions", e)
        }
      }
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
        isExpanded = false
        isSearchLoading = true
        scope.launch {
          try {
            searchResults = searchYoutube(textFieldState.text.toString().trim())
          } finally {
            isSearchLoading = false
          }
        }
      })

    SearchResults(
      searchResults = searchResults, 
      isLoading = isSearchLoading,
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 64.dp)
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
  suggestions: MutableState<List<String>>,
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
      trailingIcon = {
        IconButton(onClick = onSearch) {
          Icon(
            Icons.AutoMirrored.Filled.Send, contentDescription = null
          )
        }
      },
    )
  }

  DockedSearchBar(modifier = modifier,
    inputField = inputField,
    onExpandedChange = onExpandedChange,
    expanded = isExpanded,
    content = {
      SearchSuggestions(
        suggestions = suggestions, textFieldState = textFieldState, doSearch = onSearch
      )
    })
}

@Composable
fun SearchResults(
  searchResults: List<Song>, 
  isLoading: Boolean,
  modifier: Modifier = Modifier
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
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {}
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize()
      ) {

        items(searchResults) { song ->
          SongItem(song = song)
        }
      }
    }
  }
}

@Preview
@Composable
fun SongPreview() {


  SongItem(
    RickAstley
  )
}

@Composable
fun SearchSuggestions(
  suggestions: MutableState<List<String>>, textFieldState: TextFieldState, doSearch: () -> Unit
) {
  Column(Modifier.verticalScroll(rememberScrollState())) {
    suggestions.value.forEach { suggestion ->
      SuggestionItem(suggestion = suggestion, onClick = {
        textFieldState.setTextAndPlaceCursorAtEnd(suggestion)
        doSearch()
      })
    }
  }
}

@Composable
fun SuggestionItem(
  suggestion: String, onClick: () -> Unit
) {
  ListItem(
    headlineContent = { Text(suggestion) },
    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    leadingContent = { Icon(Icons.Default.Search, contentDescription = null) },
    modifier = Modifier
      .clickable(onClick = onClick)
      .fillMaxWidth()
  )
}