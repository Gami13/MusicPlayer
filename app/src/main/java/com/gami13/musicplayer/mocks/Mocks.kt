package com.gami13.musicplayer.mocks

import android.graphics.Bitmap
import com.gami13.musicplayer.Song
import com.gami13.musicplayer.utilities.YoutubeSearchResult
import kotlinx.datetime.LocalDateTime
import java.io.ByteArrayOutputStream
import androidx.core.graphics.createBitmap


val NeverGonnaGiveYouUp = YoutubeSearchResult(
  videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
  title = "Never Gonna Give You Up",
  duration = 213,
  uploader = "Rick Astley",
  bestThumbanilUrl = "https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg",
  viewCount = 0,
  publishedAt = LocalDateTime.parse("2022-01-01T00:00:00")

)
val NeverGonnaGiveYouUpOld = Song(
  youtubeId = "dQw4w9WgXcQ",
  title = "Never Gonna Give You Up",
  artist = "Rick Astley",
  album = "Whenever You Need Somebody",
  genre = "Pop",
  year = 1987,
  storagePath = "",
  duration = 213,
  //blue
  cover = createBlue(),
  isFavorite = false,
  publishedAt = ("2022-01-01T00:00:00")
)
val ExampleSuggestions = listOf(
  "Despacito",
  "Lose Yourself",
  "Bohemian Rhapsody",
  "Hotel California",
  "Stairway to Heaven",
  "All I Want for Christmas Is You",
  "Somebody Once Told Me"
)


 fun createBlue(): ByteArray {

  val bmp = createBitmap(100, 100)
  bmp.eraseColor(android.graphics.Color.BLUE)
  val outputStream = ByteArrayOutputStream()
  bmp.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, outputStream)
  return outputStream.toByteArray()


}