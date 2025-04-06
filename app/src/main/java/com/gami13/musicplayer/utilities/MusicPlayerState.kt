package com.gami13.musicplayer.utilities

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.gami13.musicplayer.Song

class MusicPlayerState(
  isShown: Boolean = false,
  isPlaying: Boolean = false,
  queue: List<Song> = emptyList(),
  currentSongIdx: Int = -1,
) {
  // Convert properties to mutableStateOf to make them observable
  var isShown by mutableStateOf(isShown)
    private set
  var isPlaying by mutableStateOf(isPlaying)
    private set
  var queue by mutableStateOf(queue)
    private set
  var currentSongIdx by mutableStateOf(currentSongIdx)
    private set

  fun show() {
    isShown = true
    log()

  }

  fun toggle() {
    isShown = !isShown
    log()

  }

  fun enqueue(songs: List<Song>) {

    queue + songs
    if (currentSongIdx == -1) {
      currentSongIdx = 0
    }
    log()

  }

  fun enqueue(song: Song) {
    queue = queue + song
    if (currentSongIdx == -1) {
      currentSongIdx = 0
    }
    log()
  }

  fun log() {
    println("isShown: $isShown")
    println("isPlaying: $isPlaying")
    println("queue: $queue")
    println("currentSongIdx: $currentSongIdx")
  }

}
