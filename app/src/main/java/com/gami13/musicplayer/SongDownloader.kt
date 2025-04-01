package com.gami13.musicplayer

import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists


/**
 * Downloads songs from YouTube and saves them to the music directory.
 *
 * @param songUrl YouTube URL of the song to download
 * @param onProgress Callback for progress updates
 * @param onComplete Callback when download and save are complete
 * @param onError Callback for error handling
 */
class SongDownloader(
  private val songUrl: String,
  private val onProgress: (progress: Float, eta: Long, message: String) -> Unit,
  private val onComplete: () -> Unit,
  private val onError: (error: String) -> Unit,

  ) {

  private val TAG = "SongDownloader"
  private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
  private val ytdlp = YoutubeDL.getInstance()
  private var isDownloading = false
  private val songId = songUrl.substringAfter("v=")
  private val processId = "SongDownloader_$songId"
  private val storagePath = MainActivity.settingsRepository.musicDirectory
  private val youtubeDLDir = File(
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
    "temporary_downloads"
  )
  private val temporaryPath = youtubeDLDir.absolutePath + "/$processId.mp3"
  private var didAskToSave = false
  private var songDetails: Song? = null

  private var didFinish = false

  private var count = 0


  private val _onProgress: (progress: Float, eta: Long, message: String) -> Unit =
    { progress, eta, message ->
      onProgress(progress, eta, message)
      if (progress == 100f) count++
      //Yes this is a magic number, i manually counted how many 100% messages there are before
      // its safe to use
      if (progress == 100f && count > 3) {
        didFinish = true
        if (didAskToSave) {
          _saveSong()
        }

        Log.d(TAG, "Finished $didFinish $didAskToSave")
      }
    }


  private fun errorCallback(error: String) {
    Log.e(TAG, "Error: $error")
    isDownloading = false
    cleanupTemporaryFiles()
    onError(error)
  }

  suspend fun startDownload() = withContext(Dispatchers.IO) {
    if (didFinish) {
      errorCallback("Already finished")
      return@withContext
    }
    if (isDownloading) {
      errorCallback("Already downloading")
      return@withContext
    }
    if (storagePath.first() == "") {
      errorCallback("Storage path is empty")
      return@withContext
    }
    isDownloading = true

    val request =
      YoutubeDLRequest(songUrl).addOption("-o", temporaryPath).addOption("--extract-audio")
        .addOption("--audio-format", "mp3").addOption("--no-check-certificate")
        .addOption("--ignore-errors")
//    .addOption("--audio-quality", "0")
    try {

      ytdlp.execute(request, callback = _onProgress, processId = processId)
    } catch (e: Exception) {
      errorCallback("Error executing request: ${e.message}")
      isDownloading = false
      return@withContext
    }
  }

  fun saveSong(song: Song) {
    Log.d(TAG, "Saving song")
    if (!isDownloading) {
      onError("Not downloading")
      return
    }

    didAskToSave = true
    songDetails = song
    if (didFinish) {
      _saveSong()
    }


  }


  @OptIn(DelicateCoroutinesApi::class)
  fun _saveSong() {
    if (songDetails == null) {
      onError("Song details are null")
      return
    }

    coroutineScope.launch {
      try {
        val tempFile = File(temporaryPath)
        if (!tempFile.exists()) {
          onError("Temporary file not found: $temporaryPath")
          return@launch
        }

        val storageUri = storagePath.first().toUri()

        val safeFilename = songDetails!!.title.replace(Regex("[^a-zA-Z0-9.-]"), "_") + ".mp3"


        val rootDir = DocumentFile.fromTreeUri(MainActivity.appContext, storageUri)
        if (rootDir == null || !rootDir.exists()) {
          onError("Cannot access storage directory")
          return@launch
        }

        val destinationFile = rootDir.createFile("audio/mpeg", safeFilename)
        if (destinationFile == null) {
          onError("Failed to create destination file")
          return@launch
        }

        val contentResolver = MainActivity.appContext.contentResolver

        contentResolver.openOutputStream(destinationFile.uri)?.use { outputStream ->
          tempFile.inputStream().use { inputStream ->
            inputStream.copyTo(outputStream)
          }
        } ?: run {
          onError("Failed to open output stream")
          return@launch
        }

        val songPath = destinationFile.uri.toString()
        songDetails = songDetails!!.copy(storagePath = songPath)
        MainActivity.db.songDao().insert(songDetails!!)

        cleanupTemporaryFiles()
        withContext(Dispatchers.Main) {
          isDownloading = false
          onComplete()
        }
      } catch (e: Exception) {
        onError("Failed to save song: ${e.message}")
      }
    }
  }

  fun cancelDownload() {
    if (!isDownloading) {
      onError("Not downloading")
      return
    }

    ytdlp.destroyProcessById(processId)
    cleanupTemporaryFiles()
    isDownloading = false
  }

  fun cleanupTemporaryFiles() {
    Log.d(TAG, "Cleaning up temporary files")
    Path(temporaryPath).deleteIfExists()

  }


}