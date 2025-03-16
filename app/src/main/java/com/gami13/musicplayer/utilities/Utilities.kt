package com.gami13.musicplayer.utilities

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.gami13.musicplayer.Song
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

fun Song.getThumbnailImageBitmap(): ImageBitmap {
  return BitmapFactory.decodeByteArray(cover, 0, cover.size).asImageBitmap()
}

/**
 * Format a LocalDateTime into a human-readable "time ago" string
 */
fun LocalDateTime.formatTimeAgo(): String {
  val now = Clock.System.now()
  val publishedInstant = this.toInstant(TimeZone.currentSystemDefault())
  val duration = now - publishedInstant
  
  return when {
    duration < 1.hours -> "Just now"
    duration < 24.hours -> "${duration.inWholeHours}h ago"
    duration < 30.days -> "${duration.inWholeDays}d ago"
    duration < 365.days -> "${(duration.inWholeDays / 30)}mo ago"
    else -> "${(duration.inWholeDays / 365)}y ago"
  }
}

/**
 * Format video duration from seconds to MM:SS format
 */
fun Int.formatDuration(): String {
  val minutes = this / 60
  val seconds = this % 60
  return "$minutes:${seconds.toString().padStart(2, '0')}"
}