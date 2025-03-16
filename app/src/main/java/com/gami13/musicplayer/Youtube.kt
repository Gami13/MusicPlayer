package com.gami13.musicplayer

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.SETTINGS_NAME)
class SettingsRepository(private val context: Context) {

  companion object {
    private val MUSIC_DIRECTORY_KEY = stringPreferencesKey("music_directory")
  }

  val musicDirectory: Flow<String> = context.dataStore.data
    .map { preferences -> preferences[MUSIC_DIRECTORY_KEY]  ?: "" }


  suspend fun saveMusicDirectory(directoryUri: String) {
    context.dataStore.edit { preferences ->
      preferences[MUSIC_DIRECTORY_KEY] = directoryUri
    }
  }
}



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
  val totalResults: Int,
  val resultsPerPage: Int
)

@Serializable
data class SearchResultItem(
  val kind: String,
  val etag: String,
  val id: VideoId,
  val snippet: VideoSnippet
)

@Serializable
data class VideoId(
  val kind: String,
  val videoId: String
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
  val default: Thumbnail,
  val medium: Thumbnail,
  val high: Thumbnail
)

@Serializable
data class Thumbnail(
  val url: String,
  val width: Int,
  val height: Int
)

