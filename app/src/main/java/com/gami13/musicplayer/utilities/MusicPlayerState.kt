package com.gami13.musicplayer.utilities

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.Song

class MusicPlayerState(
  isShown: Boolean = false,
  isPlaying: Boolean = false,
  queue: List<Song> = emptyList(),
  currentSongIdx: Int = -1,
) {
  companion object {
    private const val TAG = "MusicPlayerState"
  }

  // Convert properties to mutableStateOf to make them observable
  var isShown by mutableStateOf(isShown)
    private set
  var isPlaying by mutableStateOf(isPlaying)
  var queue by mutableStateOf(queue)
    private set
  var currentSongIdx by mutableIntStateOf(currentSongIdx)

  // Track current progress
  var currentPositionSeconds by mutableIntStateOf(0)
  var durationSeconds by mutableIntStateOf(0)

  // Initialize progress tracking
  init {
    MainActivity.audioPlayerManager?.setOnProgressUpdateListener { position, duration ->
      currentPositionSeconds = position
      durationSeconds = duration
      Log.d(TAG, "Progress update: $position / $duration")
    }
  }

  fun show() {
    isShown = true
    log()
  }

  fun toggle() {
    isShown = !isShown
    log()
  }

  fun enqueue(songs: List<Song>) {
    queue = queue + songs
    if (currentSongIdx == -1) {
      currentSongIdx = 0
      loadCurrentSong()
    }
    log()
  }

  fun enqueue(song: Song) {
    val wasEmpty = queue.isEmpty()
    queue = queue + song
    if (currentSongIdx == -1) {
      currentSongIdx = 0
      loadCurrentSong()
    }
    log()

    // If this was the first song added, load and play it
    if (wasEmpty) {
      playOrPause()
    }
  }

  fun playOrPause() {
    if (queue.isEmpty() || currentSongIdx < 0 || currentSongIdx >= queue.size) {
      Log.w(TAG, "Cannot play/pause: No songs in queue or invalid index")
      return
    }

    if (isPlaying) {
      MainActivity.audioPlayerManager?.pausePlayback()
    } else {
      MainActivity.audioPlayerManager?.resumePlayback()
    }
  }

  fun playNext() {
    if (queue.isEmpty()) return

    MainActivity.audioPlayerManager?.playNext()
  }

  fun playPrevious() {
    if (queue.isEmpty()) return

    MainActivity.audioPlayerManager?.playPrevious()
  }

  fun seekTo(seconds: Int) {
    Log.d(
      TAG,
      "Seeking to $seconds seconds (current position: $currentPositionSeconds, duration: $durationSeconds)"
    )
    MainActivity.audioPlayerManager?.seekTo(seconds)
    if (MainActivity.audioPlayerManager != null) {
      Log.d("TAG", "AudioPlayerManager is not null")
    }
  }

  private fun loadCurrentSong() {
    if (queue.isEmpty() || currentSongIdx < 0 || currentSongIdx >= queue.size) {
      Log.w(TAG, "Cannot load song: No songs in queue or invalid layerManager is not nullindex")
      return
    }

    MainActivity.audioPlayerManager?.loadSong(queue[currentSongIdx])
  }

  fun log() {
    Log.d(TAG, "isShown: $isShown")
    Log.d(TAG, "isPlaying: $isPlaying")
    Log.d(TAG, "queue size: ${queue.size}")
    Log.d(TAG, "currentSongIdx: $currentSongIdx")
  }
}
