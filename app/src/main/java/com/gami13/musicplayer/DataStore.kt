package com.gami13.musicplayer

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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