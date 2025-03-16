package com.gami13.musicplayer

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
val LocalElevationLevel = staticCompositionLocalOf { 0 }
@Composable
fun Container(
    modifier: Modifier = Modifier,
    // Default to the parent's elevation + 1, or explicitly set a level
    elevationLevel: Int = LocalElevationLevel.current + 1,
    shape: Shape = MaterialTheme.shapes.medium,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(color),
    tonalElevation: Dp = when(elevationLevel) {
        0 -> 0.dp
        1 -> 1.dp
        2 -> 3.dp
        3 -> 6.dp
        4 -> 8.dp
        5 -> 12.dp
        else -> 1.dp
    },
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalElevationLevel provides elevationLevel) {
        Surface(
            modifier = modifier,
            shape = shape,
            color = color,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            shadowElevation = shadowElevation,
            border = border,
            content = content
        )
    }
}