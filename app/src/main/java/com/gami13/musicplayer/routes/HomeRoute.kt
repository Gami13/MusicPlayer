package com.gami13.musicplayer.routes


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.Song
import com.gami13.musicplayer.SongDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Preview(showBackground = true)
@Composable
fun HomeRoute(modifier: Modifier = Modifier) {
  val downloader = SongDownloader(
    "https://www.youtube.com/watch?v=xFYQQPAOz7Y",
    onProgress = { progress, eta, idk ->
      println("Progress: $progress, ETA: $eta, IDK: $idk")
    },
    onComplete = {},
    onError = {},
  )
  val scope = rememberCoroutineScope()
  Column {

    Text(
      text = "HomeRoute",
      modifier = modifier
    )
    Button(onClick = {

      scope.launch {
        downloader.startDownload()
      }
    }) { Text(text = "Download") }
    Button(onClick = {
      scope.launch {
        downloader.saveSong(
          Song(

            title = "Never Gonna Give You Up",
            artist = "Rick Astley",
            duration = 3 * 60 + 33,
            cover = ByteArray(0),
            youtubeId = "xFYQQPAOz7Y",

            )
        )
      }
    }) { Text(text = "Save") }
    Button(onClick = {
      scope.launch {
        downloader.cancelDownload()
      }
    }) { Text(text = "Cancel") }

    Button(onClick = {
      scope.launch {
        withContext(Dispatchers.IO) {
          Log.d("HomeRoute", "Getting songs")
          MainActivity.db.songDao().getAll().forEach {

            Log.d("HomeRoute", it.toString())
          }
        }
      }
    }) {
      Text(text = "Get Songs")
    }


    Icon(Icons.Default.Home, contentDescription = null)
  }
}