package com.gami13.musicplayer.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.gami13.musicplayer.R
import com.gami13.musicplayer.Song
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.io.ByteArrayOutputStream
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

fun Song.getThumbnailImageBitmap(): ImageBitmap {
  return BitmapFactory.decodeByteArray(cover, 0, cover.size).asImageBitmap()
}

/**
 * Format a LocalDateTime into a human-readable "time ago" string
 */
fun LocalDateTime.formatTimeAgo(context: Context): String {
  val now = Clock.System.now()
  val publishedInstant = this.toInstant(TimeZone.currentSystemDefault())
  val duration = now - publishedInstant

  return when {
    duration < 1.hours -> context.getString(R.string.just_now)
    duration < 24.hours -> context.getString(R.string.hours_ago, duration.inWholeHours.toString())
    duration < 30.days -> context.getString(R.string.days_ago, duration.inWholeDays.toString())
    duration < 365.days -> context.getString(
      R.string.months_ago,
      (duration.inWholeDays / 30).toString()
    )

    else -> context.getString(R.string.years_ago, (duration.inWholeDays / 365).toString())
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

fun Bitmap.toByteArray(): ByteArray {
  val stream = ByteArrayOutputStream()
  this.compress(Bitmap.CompressFormat.PNG, 100, stream)
  return stream.toByteArray()
}

fun ByteArray.toBitmap(): Bitmap {
  return BitmapFactory.decodeByteArray(this, 0, this.size)
}