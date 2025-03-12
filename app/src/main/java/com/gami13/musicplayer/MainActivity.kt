package com.gami13.musicplayer

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


  companion object {
    val httpClient = HttpClient(CIO)
    lateinit var appContext: Context
    var FAB: @Composable () -> Unit by mutableStateOf({})
    var TopAppBar: @Composable () -> Unit by mutableStateOf({})
    lateinit var db: AppDatabase
    lateinit var openDocumentTree: ActivityResultLauncher<Uri?>
    val settingsRepository by lazy {
      SettingsRepository(
        context = appContext
      )
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {

    appContext = applicationContext
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      App(
        fab = FAB,
        topAppBar = TopAppBar
      )
    }
    db = Room.databaseBuilder(
      applicationContext, AppDatabase::class.java, "music-player"
    ).build()

    openDocumentTree =
      registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
          lifecycleScope.launch {
            settingsRepository.saveMusicDirectory(uri.toString())
          }
        }
      }
  }
}


