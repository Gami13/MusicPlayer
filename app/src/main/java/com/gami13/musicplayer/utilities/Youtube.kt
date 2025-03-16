package com.gami13.musicplayer.utilities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.gami13.musicplayer.Constants
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.Song
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import java.io.ByteArrayOutputStream


@Serializable
data class YoutubeSearchResponse(
  val kind: String,
  val etag: String,
  val nextPageToken: String,
  val regionCode: String,
  val pageInfo: PageInfo,
  val items: List<SearchResultItem>
)

@Serializable
data class PageInfo(
  val totalResults: Int, val resultsPerPage: Int
)

@Serializable
data class SearchResultItem(
  val kind: String, val etag: String, val id: VideoId, val snippet: VideoSnippet
)

@Serializable
data class VideoId(
  val kind: String, val videoId: String
)

@Serializable
data class VideoSnippet(
  val publishedAt: String,
  val channelId: String,
  val title: String,
  val description: String,
  val thumbnails: Thumbnails,
  val channelTitle: String,
  val liveBroadcastContent: String,
  val publishTime: String
)

@Serializable
data class Thumbnails(
  val default: Thumbnail, val medium: Thumbnail, val high: Thumbnail
)

@Serializable
data class Thumbnail(
  val url: String, val width: Int, val height: Int
)

//downloads the thumbnail and optimizes it to webp
suspend fun downloadThumbnail(url: String): ByteArray {
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


suspend fun searchYoutube(query: String): List<Song> {
  val songs = mutableListOf<Song>()
  try {
    val url = Constants.YOUTUBE_SEARCH_BASE_URL + query.replace(" ", "+")

    val response = MainActivity.httpClient.get(url)
    if (!response.status.isSuccess()) {
      Log.e("SearchFunction", "API request failed with status: ${response.status}")
      return emptyList()
    }

    val responseBody = response.body<String>()
    val result = Json.decodeFromString<YoutubeSearchResponse>(responseBody)

    result.items.forEach { item ->
      try {
        val thumbnailUrl = with(item.snippet.thumbnails) {
          high.url.takeIf { it.isNotEmpty() }
            ?: medium.url.takeIf { it.isNotEmpty() }
            ?: default.url.takeIf { it.isNotEmpty() }
            ?: ""
        }

        val publishedAt = try {
          Instant.parse(item.snippet.publishedAt).toLocalDateTime(TimeZone.currentSystemDefault())
        } catch (e: Exception) {
          Log.w("SearchFunction", "Failed to parse date: ${item.snippet.publishedAt}", e)
          Instant.parse("1970-01-01T00:00:00").toLocalDateTime(TimeZone.currentSystemDefault())
        }

        val thumbnailBytes = if (thumbnailUrl.isNotEmpty()) {
          withContext(Dispatchers.IO) {
            try {
              downloadThumbnail(thumbnailUrl)
            } catch (e: Exception) {
              Log.w("SearchFunction", "Failed to download thumbnail: $thumbnailUrl", e)
              ByteArray(0)
            }
          }
        } else ByteArray(0)
        val song = Song(
          youtubeId = item.id.videoId,
          title = item.snippet.title,
          artist = item.snippet.channelTitle,
          album = "",
          genre = "",
          year = publishedAt.year,
          storagePath = "",
          duration = 0,
          cover = thumbnailBytes,
          isFavorite = false,
          publishedAt = publishedAt.toString()
        )

        songs.add(song)
        Log.d("SearchFunction", "Found song: $song")
        Log.d("SearchFunction", "Published at: ${item.snippet.publishedAt}")
        Log.d("SearchFunction", "Published time: ${item.snippet.publishTime}")
      } catch (e: Exception) {
        Log.e("SearchFunction", "Error processing search result item", e)
      }
    }

    Log.d("SearchFunction", "Found ${songs.size} songs")
  } catch (e: Exception) {
    Log.e("SearchFunction", "Error searching YouTube", e)
  }

  return songs
}