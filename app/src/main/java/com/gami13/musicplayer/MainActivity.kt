package com.gami13.musicplayer

import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {


  companion object {
    val httpClient = HttpClient(CIO)
    lateinit var appContext: Context
    var FAB: @Composable () -> Unit by mutableStateOf({})
    var TopAppBar: @Composable () -> Unit by mutableStateOf({})
    lateinit var db: AppDatabase
    lateinit var openDocumentTree: ActivityResultLauncher<Uri?>

    private lateinit var pickMediaLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mediaPickerCallBack: (Uri) -> Unit
    lateinit var pickMedia: (
      media: PickVisualMedia.VisualMediaType, callback: (Uri) -> Unit
    ) -> Unit


    lateinit var contentResolver: ContentResolver

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
        fab = FAB, topAppBar = TopAppBar
      )
    }
    db = Room.databaseBuilder(
      applicationContext, AppDatabase::class.java, "music-player"
    ).build()

    Companion.contentResolver = contentResolver

    pickMediaLauncher = registerForActivityResult(PickVisualMedia()) { uri ->
      if (uri != null) {
        Log.d("PhotoPicker", "Selected URI: $uri")
        mediaPickerCallBack(uri)
      } else {
        Log.d("PhotoPicker", "No media selected")
      }
    }

    pickMedia = { media: PickVisualMedia.VisualMediaType, callback: (Uri) -> Unit ->
      mediaPickerCallBack = callback
      pickMediaLauncher.launch(PickVisualMediaRequest(media))
    }

    openDocumentTree =
      registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
          appContext.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
          )

          lifecycleScope.launch {
            settingsRepository.saveMusicDirectory(uri.toString().replace("%3A", ":"))

          }
        }
      }

    try {
      YoutubeDL.getInstance().init(this)
      FFmpeg.getInstance().init(this);
      CoroutineScope(Dispatchers.IO).launch {

        YoutubeDL.getInstance().updateYoutubeDL(appContext)
        Log.d("MAIN", "Version " + YoutubeDL.getInstance().version(appContext).toString())
      }
    } catch (e: YoutubeDLException) {
      Log.d(TAG, "failed to initialize youtubedl-android", e)
    }


  }
}


