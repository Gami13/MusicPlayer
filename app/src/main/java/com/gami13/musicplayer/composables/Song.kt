package com.gami13.musicplayer.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.gami13.musicplayer.R
import com.gami13.musicplayer.mocks.NeverGonnaGiveYouUp
import com.gami13.musicplayer.utilities.YoutubeSearchResult
import com.gami13.musicplayer.utilities.formatDuration
import com.gami13.musicplayer.utilities.formatTimeAgo

@Composable
fun SongThumbnail(song: YoutubeSearchResult) {
  Box(
    modifier = Modifier.heightIn(min = 56.dp)
  ) {
    AsyncImage(
      model = song.bestThumbanilUrl,
      contentDescription = stringResource(R.string.thumbnail_for, song.title),
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .size(56.dp, 56.dp)
        .clip(RoundedCornerShape(12.dp))
    )
  }
}


@Composable
fun SongMetadata(song: YoutubeSearchResult) {
  val separator = @Composable {
    Text(
      text = "•", fontSize = 11.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }

  Row(
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(top = 2.dp)
  ) {
    Text(
      text = (song.uploader.take(28) + if(song.uploader.length>28)"…" else {
        ""
      }) , fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      maxLines = 1, overflow = TextOverflow.Ellipsis,
    )

    if (song.duration > 0) {
      separator()

      Text(
        text = song.duration.formatDuration(), fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    separator()

    Text(
      text = song.publishedAt.formatTimeAgo(),
      fontSize = 11.sp,
//      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}


@Composable
fun SongItem(song: YoutubeSearchResult, onClick: () -> Unit = {}) {
  Row(
    modifier = Modifier
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp)

  ) {
    SongThumbnail(song = song)
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(end = 8.dp),
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = song.title,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontSize = 14.sp
      )

      SongMetadata(song = song)
    }

    IconButton(
      onClick = onClick,
      modifier = Modifier.size(36.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Download,
        contentDescription = stringResource(R.string.download_song),
        tint = MaterialTheme.colorScheme.primary,
      )
    }
  }
}


@Composable
@PreviewLightDark()
fun SongPreview() {
  Previewer {

    SongItem(NeverGonnaGiveYouUp)
  }
}