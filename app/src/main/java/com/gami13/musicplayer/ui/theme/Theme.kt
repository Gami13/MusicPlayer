package com.gami13.musicplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


@Composable
fun MusicPlayerTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {


  val colorScheme =
    if (!darkTheme) {
      dynamicLightColorScheme(LocalContext.current)
    } else {
      dynamicDarkColorScheme(LocalContext.current)
    }

  MaterialTheme(
    colorScheme = colorScheme,
//    typography = Typography,
    content = content
  )
}