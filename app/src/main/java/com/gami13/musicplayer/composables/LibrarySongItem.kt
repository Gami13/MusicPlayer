package com.gami13.musicplayer.composables

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
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
import com.gami13.musicplayer.R
import com.gami13.musicplayer.Song
import com.gami13.musicplayer.mocks.createBlue
import com.gami13.musicplayer.utilities.formatDuration
import com.gami13.musicplayer.utilities.getThumbnailImageBitmap


@Composable
fun LibrarySongItem(
    song: Song,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Thumbnail for the song
        SongCoverArt(song)
        
        // Song details
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
            
            // Song metadata (artist, duration)
            SongDetails(song)
        }
        
        // Play button
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = stringResource(R.string.play_song),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun SongCoverArt(song: Song) {
    Box(
        modifier = Modifier.heightIn(min = 56.dp),
        contentAlignment = Alignment.Center
    ) {
        if (song.cover.isNotEmpty()) {
            androidx.compose.foundation.Image(
                bitmap = song.getThumbnailImageBitmap(),
                contentDescription = stringResource(R.string.thumbnail_for, song.title),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp, 56.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        } else {
            // Display a placeholder if no cover is available
            Box(
                modifier = Modifier
                    .size(56.dp, 56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SongDetails(song: Song) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 2.dp)
    ) {
        // Artist
        Text(
            text = song.artist.takeIf { it.isNotEmpty() } ?: stringResource(R.string.unknown_artist),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        // Separator
        Text(
            text = "•",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Duration
        if (song.duration > 0) {
            Text(
                text = song.duration.formatDuration(),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Album (if available)
        if (song.album.isNotEmpty()) {
            Text(
                text = "•",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = song.album,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
@PreviewLightDark
fun LibrarySongItemPreview() {
    Previewer {
        LibrarySongItem(
            song = Song(
                id = 1,
                title = "Never Gonna Give You Up",
                artist = "Rick Astley",
                album = "Whenever You Need Somebody",
                duration = 213,
                cover = createBlue(),
                year = 1987
            )
        )
    }
}