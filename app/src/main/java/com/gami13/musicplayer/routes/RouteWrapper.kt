package com.gami13.musicplayer.routes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.composables.miniHeight


@Composable
fun RouteWrapper(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  Box(
    modifier
      .fillMaxSize()
      .padding(
        top = 8.dp, start = 8.dp, end = 8.dp, bottom = 8.dp + if (MainActivity.musicPlayerState.isShown)
          miniHeight
        else 0.dp
      ),
  ) {
    content()

  }
}