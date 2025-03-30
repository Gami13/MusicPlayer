package com.gami13.musicplayer.composables.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gami13.musicplayer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
  modifier: Modifier = Modifier,
  textFieldState: TextFieldState,
  isExpanded: Boolean,
  onExpandedChange: (Boolean) -> Unit,
  suggestions: List<String>,
  onSearch: () -> Unit,
  cancelSearch: () -> Unit = {}
) {
  // Use surface color with elevation for consistent appearance
  val containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
  
  // Create the input field composable separately for readability
  val searchInputField = @Composable {
    SearchBarDefaults.InputField(
      modifier = Modifier.fillMaxWidth(),
      state = textFieldState,
      onSearch = { onSearch() },
      expanded = isExpanded,
      onExpandedChange = onExpandedChange,
      placeholder = { Text(stringResource(R.string.hinted_search_text)) },
      leadingIcon = { 
        Icon(
          Icons.Default.Search, 
          contentDescription = stringResource(R.string.search)
        ) 
      },
      trailingIcon = {
        if (textFieldState.text.isNotEmpty()) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = stringResource(R.string.clear_search),
            modifier = Modifier
              .clickable { cancelSearch() }
              .padding(8.dp)
          )
        }
      },
      colors = SearchBarDefaults.inputFieldColors(
        unfocusedContainerColor = containerColor,
        focusedContainerColor = containerColor,
      )
    )
  }

  // Main search bar component
  DockedSearchBar(
    modifier = modifier,
    inputField = searchInputField,
    onExpandedChange = onExpandedChange,
    expanded = isExpanded,
    colors = SearchBarDefaults.colors(containerColor = containerColor),
    content = {
      SearchSuggestions(
        suggestions = suggestions, 
        textFieldState = textFieldState, 
        doSearch = onSearch
      )
    }
  )
}