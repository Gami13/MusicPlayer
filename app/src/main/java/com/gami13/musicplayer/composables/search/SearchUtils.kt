package com.gami13.musicplayer.composables.search

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

// Non-composable function that can be called from a background thread
fun highlightSuggestion(
  suggestion: String,
  query: String,
  primaryColor: Color
): AnnotatedString {
  val startIndex = suggestion.indexOf(query, ignoreCase = true)
  return if (startIndex == -1 || query.isEmpty()) {
    AnnotatedString(suggestion)
  } else {
    buildAnnotatedString {
      append(suggestion.substring(0, startIndex))
      withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = primaryColor)) {
        append(suggestion.substring(startIndex, startIndex + query.length))
      }
      append(suggestion.substring(startIndex + query.length))
    }
  }
}