package com.gami13.musicplayer.composables

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.gami13.musicplayer.ui.theme.MusicPlayerTheme

@Composable
fun Previewer(content: @Composable () -> Unit) {
  MusicPlayerTheme {
    Surface {
      content()
    }

  }
}