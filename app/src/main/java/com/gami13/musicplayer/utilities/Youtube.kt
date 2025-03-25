@file:OptIn(ExperimentalSerializationApi::class)

package com.gami13.musicplayer.utilities

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.gami13.musicplayer.Constants
import com.gami13.musicplayer.MainActivity.Companion.httpClient
import com.gami13.musicplayer.Song
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames
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

suspend fun downloadThumbnail(url: String): ByteArray {
  return try {

    val response: HttpResponse = httpClient.get(url)
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


suspend fun getAutoCompleteSuggestions(query: String): List<String> {
  if (query.isBlank()) return emptyList()

  try {
    val url = Constants.YOUTUBE_SUGGESTION_BASE_URL + query.replace(" ", "+")
    val response = httpClient.get(url).body<String>()
    val jsonContent = response.substringAfter("window.google.ac.h(").substringBeforeLast(")")
    return parseYouTubeSuggestions(jsonContent)
  } catch (e: Exception) {
    Log.e(TAG, "Error fetching suggestions", e)
    return emptyList()
  }
}

private fun parseYouTubeSuggestions(response: String): List<String> {
  if (response.isBlank()) return emptyList()

  return try {
    val jsonArray = Json.parseToJsonElement(response).jsonArray
    if (jsonArray.size < 2) return emptyList()

    val suggestionsArray = jsonArray[1].jsonArray
    suggestionsArray.mapNotNull {
      try {
        it.jsonArray[0].jsonPrimitive.content
      } catch (_: Exception) {
        null
      }
    }
  } catch (e: Exception) {
    Log.e(TAG, "Error parsing suggestions", e)
    emptyList()
  }
}

@Deprecated("use searchYoutube instead")
suspend fun searchYoutubeOld(query: String): List<Song> {
  val songs = mutableListOf<Song>()
  try {
    val url = Constants.YOUTUBE_SEARCH_BASE_URL + query.replace(" ", "+")

    val response = httpClient.get(url)
    if (!response.status.isSuccess()) {
      Log.e("SearchFunction", "API request failed with status: ${response.status}")
      return emptyList()
    }

    val responseBody = response.body<String>()
    val result = Json.decodeFromString<YoutubeSearchResponse>(responseBody)

    result.items.forEach { item ->
      try {
        val thumbnailUrl = getBestThumbnailUrl(item.snippet.thumbnails)


        val publishedAt = parsePublishedDate(item.snippet.publishedAt)

        val thumbnailBytes = downloadThumbnailIfAvailable(thumbnailUrl)
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
      } catch (e: Exception) {
        Log.e("SearchFunction", "Error processing search result item", e)
      }
    }

  } catch (e: Exception) {
    Log.e("SearchFunction", "Error searching YouTube", e)
  }

  return songs
}

@Serializable
data class YoutubeSearchResult(
  var videoUrl: String = "",
  var title: String = "",
  var duration: Int = 0,
  var uploader: String = "",
  var bestThumbanilUrl: String = "",
  var viewCount: Int = 0,
  var publishedAt: LocalDateTime
)

@Serializable

private data class RawThumbnail(
  var url: String = "", var width: Int = 0, var height: Int = 0
)

@Serializable

private data class RawYoutubeSearchResult(
  var title: String = "",
  @JsonNames("webpage_url")
  var webpageUrl: String = "",
  var duration: Float = 0.0F,
  var uploader: String = "",
  var thumbnails: List<RawThumbnail>,
  var thumbnail: String = "",
  @JsonNames("view_count")
  var viewCount: Int = 0,
  var epoch: Int
)

suspend fun searchYoutube(query: String): List<YoutubeSearchResult> = withContext(Dispatchers.IO) {
  val limit = 20
  val request = YoutubeDLRequest("ytsearch${limit}:$query").addOption("--flat-playlist")
    .addOption("--skip-download").addOption("--quiet").addOption("--ignore-errors").addOption(
      "--print", "%(.{title,webpage_url,duration,uploader,thumbnails,view_count,epoch})j,"
    )
  val songs = mutableListOf<YoutubeSearchResult>()

  try {
    YoutubeDL.getInstance().execute(request) { a, b, video ->
      Log.d("YoutubeDL", "$a | $b | $video")
      //remove last character
      val videoData = video.substring(0, video.length - 1)
      val json = Json.decodeFromString<RawYoutubeSearchResult>(videoData)
      val bestThumbanilUrl = json.thumbnails.maxByOrNull { it.width * it.height }?.url ?: ""
      val publishedAt =
        Instant.fromEpochSeconds(json.epoch.toLong())
          .toLocalDateTime(TimeZone.currentSystemDefault())
      songs.add(
        YoutubeSearchResult(
          videoUrl = json.webpageUrl,
          title = json.title,
          duration = json.duration.toInt(),
          uploader = json.uploader,
          bestThumbanilUrl = bestThumbanilUrl,
          viewCount = json.viewCount,
          publishedAt = publishedAt
        )
      )
    }
  } catch (e: Exception) {
    Log.e("SearchYoutube", "Error searching YouTube", e)
  }

  songs
}

private fun getBestThumbnailUrl(thumbnails: Thumbnails): String {
  return with(thumbnails) {
    high.url.takeIf { it.isNotEmpty() } ?: medium.url.takeIf { it.isNotEmpty() }
    ?: default.url.takeIf { it.isNotEmpty() } ?: ""
  }
}

private fun parsePublishedDate(dateString: String): LocalDateTime {
  return try {
    Instant.parse(dateString).toLocalDateTime(TimeZone.currentSystemDefault())
  } catch (e: Exception) {
    Log.w(TAG, "Failed to parse date: $dateString", e)
    Instant.parse("1970-01-01T00:00:00").toLocalDateTime(TimeZone.currentSystemDefault())
  }
}

private suspend fun downloadThumbnailIfAvailable(url: String): ByteArray {
  if (url.isEmpty()) return ByteArray(0)

  return withContext(Dispatchers.IO) {
    try {
      downloadThumbnail(url)
    } catch (e: Exception) {
      Log.w(TAG, "Failed to download thumbnail: $url", e)
      ByteArray(0)
    }
  }
}
