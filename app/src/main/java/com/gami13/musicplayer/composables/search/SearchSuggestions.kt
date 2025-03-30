package com.gami13.musicplayer.composables.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SearchSuggestions(
  suggestions: List<String>, 
  textFieldState: TextFieldState, 
  doSearch: () -> Unit
) {
  // Store processed (highlighted) suggestions for better performance
  val processedSuggestions = remember { mutableStateMapOf<String, AnnotatedString>() }
  val currentQuery = textFieldState.text.toString()
  val primaryColor = MaterialTheme.colorScheme.primary

  // Process suggestions in background thread to prevent UI lag
  LaunchedEffect(suggestions, currentQuery) {
    withContext(Dispatchers.Default) {
      suggestions.forEach { suggestion ->
        val annotatedString = highlightSuggestion(suggestion, currentQuery, primaryColor)
        processedSuggestions[suggestion] = annotatedString
      }
    }
  }

  // Display suggestions in a scrollable column
  Column(Modifier.verticalScroll(rememberScrollState())) {
    suggestions.forEach { suggestion ->
      val displayText = processedSuggestions[suggestion] ?: AnnotatedString(suggestion)

      SuggestionItem(
        suggestion = displayText,
        onClick = {
          textFieldState.setTextAndPlaceCursorAtEnd(suggestion)
          doSearch()
        }
      )
    }
  }
}

@Composable
private fun SuggestionItem(
  suggestion: AnnotatedString,
  onClick: () -> Unit
) {
  ListItem(
    headlineContent = { Text(suggestion) },
    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    leadingContent = { 
      Icon(
        Icons.Default.Search, 
        contentDescription = null
      ) 
    },
    modifier = Modifier
      .clickable(onClick = onClick)
      .fillMaxWidth()
  )
}