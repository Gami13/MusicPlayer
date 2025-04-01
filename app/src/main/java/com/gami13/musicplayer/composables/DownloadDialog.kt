package com.gami13.musicplayer.composables

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.R
import com.gami13.musicplayer.Song
import com.gami13.musicplayer.mocks.NeverGonnaGiveYouUp
import com.gami13.musicplayer.mocks.createBlue
import com.gami13.musicplayer.utilities.YoutubeSearchResult
import com.gami13.musicplayer.utilities.downloadThumbnail
import com.gami13.musicplayer.utilities.toBitmap
import java.io.ByteArrayOutputStream

@PreviewLightDark
@Preview(wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE)
@Composable
fun DownloadDialogPreview() {
  Previewer {
    DownloadDialog(
      song = NeverGonnaGiveYouUp, onDismiss = {}, onSave = {}, getProgress = { 0f },
      getEta = { 0L })
  }
}


private fun ImageBitmap.toByteArray(): ByteArray {
  val byteArrayOutputStream = ByteArrayOutputStream()
  this.asAndroidBitmap()
    .compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, byteArrayOutputStream)
  return byteArrayOutputStream.toByteArray()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadDialog(
  song: YoutubeSearchResult,
  onDismiss: () -> Unit,
  onSave: (Song) -> Unit,
  getProgress: () -> Float,
  getEta: () -> Long,
) {

  var title by remember { mutableStateOf(song.title) }
  var artist by remember { mutableStateOf(song.uploader) }
  var album by remember { mutableStateOf("") }
  var genre by remember { mutableStateOf("") }
  var year by remember { mutableStateOf(song.publishedAt.year.toString()) }
  val scope = rememberCoroutineScope()
  var bitmap: ImageBitmap = createBlue().toBitmap().asImageBitmap()
  var shouldShowProgress by remember { mutableStateOf(false) }

  var coverBitmap by remember { mutableStateOf(bitmap) }
  LaunchedEffect(scope) {

    coverBitmap = downloadThumbnail(song.bestThumbanilUrl).toBitmap().asImageBitmap()
  }


  Dialog(onDismissRequest = onDismiss) {

    Surface(
      shape = RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surface,
      modifier = Modifier
    ) {
      Column(
        modifier = Modifier
          .padding(24.dp)
          .fillMaxWidth()
      ) {
        Text(
          text = stringResource(R.string.download_song),
          style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          Box {
            Image(
              coverBitmap, contentDescription = stringResource(R.string.cover), modifier = Modifier
                .height(200.dp)
                .width(200.dp), contentScale = ContentScale.Crop


            )

            FilledIconButton(
              onClick = {
                getImageFromPhone {
                  coverBitmap = it
                }
              },
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(2.dp)
                .zIndex(10F)
            ) {
              Icon(
                Icons.Default.AddPhotoAlternate,
                contentDescription = stringResource(R.string.change_cover_image)
              )
            }
          }
        }



        Spacer(modifier = Modifier.height(16.dp))

        // Editable fields
        TextField(
          value = title,
          onValueChange = { title = it },
          label = { Text(stringResource(R.string.title)) },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
          value = artist,
          onValueChange = { artist = it },
          label = { Text(stringResource(R.string.artist)) },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
          value = album,
          onValueChange = { album = it },
          label = { Text(stringResource(R.string.album)) },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))


        TextField(
          value = genre,
          onValueChange = { genre = it },
          label = { Text(stringResource(R.string.genre)) },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
          value = year,
          onValueChange = { it },
          label = { Text(stringResource(R.string.year)) },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))


        // Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.cancel))
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(onClick = {
            onSave(
              Song(
                youtubeId = song.videoUrl,
                title = title,
                artist = artist,
                album = album,
                genre = genre,
                year = year.toIntOrNull() ?: 0,
                storagePath = "",
                duration = song.duration.toInt(),
                cover = coverBitmap.toByteArray(),
                isFavorite = false
              )
            )
            shouldShowProgress = true


          }) {
            Text(stringResource(R.string.download))
          }


        }
        if (shouldShowProgress) {
          LinearProgressIndicator(progress = {
            getProgress() / 100f
          })
          Text(
            text = "${(getProgress().toInt())}% -- ${getEta()}s",
          )
        }
      }
    }
  }
}


fun getImageFromPhone(onImageSelected: (ImageBitmap) -> Unit) {
  MainActivity.pickMedia(PickVisualMedia.ImageOnly) {
    onImageSelected(
      ImageDecoder.decodeBitmap(
        ImageDecoder.createSource(
          MainActivity.contentResolver,
          it
        )
      ).asImageBitmap()
    )
  }

}


