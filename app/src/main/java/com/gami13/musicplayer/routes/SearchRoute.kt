package com.gami13.musicplayer.routes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.gami13.musicplayer.Constants
import com.gami13.musicplayer.Container
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.Song
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.ByteArrayOutputStream

//TODO: Refactor the hell out of this
val ExampleSuggestions = listOf(
  "Despacito",
  "Lose Yourself",
  "Bohemian Rhapsody",
  "Hotel California",
  "Stairway to Heaven",
  "All I Want for Christmas Is You",
  "Somebody Once Told Me"
)

suspend fun autoCompleteSearch(query: String): String {
  try {
    val url = Constants.YOUTUBE_SUGGESTION_BASE_URL + query.replace(" ", "+")
    val response = MainActivity.httpClient.get(url).body<String>()
    val result = response.substringAfter("window.google.ac.h(").substringBeforeLast(")")
    return result
  } catch (e: Exception) {
    Log.e("AutoCompleteSearch", "Error fetching suggestions", e)
    return ""
  }
}

fun parseYouTubeSuggestions(response: String): List<String> {
  return try {
    val jsonArray = Json.parseToJsonElement(response).jsonArray
    val suggestionsArray = jsonArray[1].jsonArray
    suggestionsArray.map { it.jsonArray[0].jsonPrimitive.content }
  } catch (e: Exception) {
    Log.e("parseYouTubeSuggestions", "Error parsing suggestions", e)
    emptyList()
  }
}

//TODO: Do the design
@Composable
fun SongItem(song: Song) {
  val thumbnailBitmap = remember(song.youtubeId) {
    if (song.cover.isNotEmpty()) {
      try {
        BitmapFactory.decodeByteArray(song.cover, 0, song.cover.size).asImageBitmap()
      } catch (_: Exception) {
        Log.d("SongItem", "Failed to decode thumbnail")
        null
      }
    } else {
      Log.d("SongItem", "No thumbnail")
      null
    }
  }

  ListItem(headlineContent = {
    Text(
      text = song.title,
      fontWeight = FontWeight.Bold,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }, supportingContent = {
    Text(
      text = song.artist, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
    )
  }, leadingContent = {
    if (thumbnailBitmap != null) {
      Image(
        bitmap = thumbnailBitmap,
        contentDescription = "Thumbnail for ${song.title}",
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(56.dp)
      )
    } else {
      // Fallback icon if no thumbnail
      Icon(
        imageVector = Icons.Default.MusicNote,
        contentDescription = null,
        modifier = Modifier.size(56.dp)
      )
    }
  }, modifier = Modifier
    .fillMaxWidth()
    .clickable(onClick = {

    })
    .padding(vertical = 4.dp)
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SearchRoute(modifier: Modifier = Modifier) {
  var searchResults by remember { mutableStateOf<List<Song>>(emptyList()) }
  val scope = rememberCoroutineScope()
  fun doSearch(query: String) {
    scope.launch {
      searchResults = search(query)
    }
    Log.d("SearchRoute", "Searching for: $query")
  }

  val textFieldState = rememberTextFieldState()
  var isExpanded by rememberSaveable { mutableStateOf(false) }

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

  val inputField = @Composable {
    SearchBarDefaults.InputField(
      modifier = Modifier.fillMaxWidth(),
      state = textFieldState,
      onSearch = { isExpanded = false },
      expanded = isExpanded,
      onExpandedChange = { isExpanded = it },
      placeholder = { Text("Hinted search text") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      trailingIcon = {
        IconButton(onClick = {
          doSearch(textFieldState.text.toString().trim())
          isExpanded = false
        }) {
          Icon(
            Icons.AutoMirrored.Filled.Send, contentDescription = null
          )
        }
      },
    )
  }

  Box(
    modifier
      .fillMaxSize()
      .padding(8.dp),
  ) {
    DockedSearchBar(modifier = Modifier
      .fillMaxWidth()
      .zIndex(10f),
      inputField = inputField,
      onExpandedChange = { expanded ->
        isExpanded = expanded
      },
      expanded = isExpanded,
      content = {
        Column(Modifier.verticalScroll(rememberScrollState())) {
          suggestions.value.forEach { suggestion ->
            ListItem(headlineContent = { Text(suggestion) },
              colors = ListItemDefaults.colors(containerColor = Color.Transparent),
              leadingContent = { Icon(Icons.Default.Search, contentDescription = null) },
              modifier = Modifier
                .clickable {
                  textFieldState.setTextAndPlaceCursorAtEnd(suggestion)
                  isExpanded = false
                  doSearch(textFieldState.text.toString().trim())
                }
                .fillMaxWidth())
          }
        }
      })

    Container(
      Modifier
        .fillMaxWidth()
        .padding(top = 64.dp)
    ) {
      if (searchResults.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
          SongPreview()


        }
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
}

private suspend fun downloadThumbnail(url: String): ByteArray {
  return try {
    val response: HttpResponse = MainActivity.httpClient.get(url)
    if (!response.status.isSuccess()) {
      Log.e("DownloadThumbnail", "Failed to download thumbnail: ${response.status}")
      return ByteArray(0)
    }

    val imageBytes = response.body<ByteArray>()

    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, outputStream)

    bitmap.recycle()

    outputStream.toByteArray()
  } catch (e: Exception) {
    Log.e("DownloadThumbnail", "Error downloading thumbnail", e)
    ByteArray(0)
  }
}

private suspend fun search(query: String): List<Song> {
  val songs = mutableListOf<Song>()
  try {
    val url = Constants.YOUTUBE_SEARCH_BASE_URL + query.replace(" ", "+")

    val response = MainActivity.httpClient.get(url)
    if (!response.status.isSuccess()) {
      Log.e("SearchFunction", "API request failed with status: ${response.status}")
      return emptyList()
    }

    val responseBody = response.body<String>()
    Log.d(
      "SearchFunction", "Got response: ${responseBody.take(100)}..."
    )

    val json = Json.parseToJsonElement(responseBody).jsonObject
    val items = json["items"]?.jsonArray ?: return emptyList()

    for (item in items) {
      val itemObj = item.jsonObject
      val id = itemObj["id"]?.jsonObject?.get("videoId")?.jsonPrimitive?.content ?: continue
      val snippet = itemObj["snippet"]?.jsonObject ?: continue

      val title = snippet["title"]?.jsonPrimitive?.content ?: ""
      val channelTitle = snippet["channelTitle"]?.jsonPrimitive?.content ?: ""
//      val description = snippet["description"]?.jsonPrimitive?.content ?: ""
      val thumbnailUrl =
        snippet["thumbnails"]?.jsonObject?.get("medium")?.jsonObject?.get("url")?.jsonPrimitive?.content
          ?: ""

      // Download thumbnail and convert to ByteArray
      val thumbnailBytes = if (thumbnailUrl.isNotEmpty()) {
        withContext(Dispatchers.IO) {
          downloadThumbnail(thumbnailUrl)
        }
      } else {
        ByteArray(0)
      }

      songs.add(
        Song(
          youtubeId = id,
          title = title,
          artist = channelTitle,
          album = "",
          genre = "",
          year = 0,
          storagePath = "",
          duration = 0,
          cover = thumbnailBytes,
          isFavorite = false
        )
      )
    }

    Log.d("SearchFunction", "Found ${songs.size} songs")
  } catch (e: Exception) {
    Log.e("SearchFunction", "Error searching YouTube", e)
  }

  return songs
}

@Preview
@Composable
fun SongPreview() {
  val thumbnail = remember {
    val bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    bmp.eraseColor(android.graphics.Color.BLUE)
    val outputStream = ByteArrayOutputStream()
    bmp.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, outputStream)
    outputStream.toByteArray()
  }

  SongItem(
    Song(
      youtubeId = "dQw4w9WgXcQ",
      title = "Never Gonna Give You Up",
      artist = "Rick Astley",
      album = "Whenever You Need Somebody",
      genre = "Pop",
      year = 1987,
      storagePath = "",
      duration = 213,
      cover = thumbnail,
      isFavorite = false
    )
  )
}