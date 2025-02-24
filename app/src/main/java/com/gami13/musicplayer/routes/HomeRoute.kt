package com.gami13.musicplayer.routes


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Composable
fun HomeRoute(modifier: Modifier = Modifier) {
	Text(
		text = "HomeRoute",
		modifier = modifier
	)
	Icon(Icons.Default.Home, contentDescription = null)
}