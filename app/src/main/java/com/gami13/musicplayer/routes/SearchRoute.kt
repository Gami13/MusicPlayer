package com.gami13.musicplayer.routes

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gami13.musicplayer.Constants
import com.gami13.musicplayer.Container
import com.gami13.musicplayer.MainActivity
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

val ExampleSuggestions = listOf(
  "Despacito",
  "Lose Yourself",
  "Bohemian Rhapsody",
  "Hotel California",
  "Stairway to Heaven",
  "All I Want for Christmas Is You",
  "Somebody Once Told Me"
)


suspend fun autoCompleteSearch(query: String): String {
  try {
    val url = Constants.YOUTUBE_SUGGESTION_BASE_URL + query.replace(" ", "+")
    val response = MainActivity.httpClient.get(url).body<String>()
    val result = response.substringAfter("window.google.ac.h(").substringBeforeLast(")")
    return result
  } catch (e: Exception) {
    Log.e("AutoCompleteSearch", "Error fetching suggestions", e)
    return ""
  }
}

fun parseYouTubeSuggestions(response: String): List<String> {
  return try {
    val jsonArray = Json.parseToJsonElement(response).jsonArray
    val suggestionsArray = jsonArray[1].jsonArray
    suggestionsArray.map { it.jsonArray[0].jsonPrimitive.content }
  } catch (e: Exception) {
    Log.e("parseYouTubeSuggestions", "Error parsing suggestions", e)
    emptyList()
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SearchRoute(modifier: Modifier = Modifier) {

  val textFieldState = rememberTextFieldState()
  val scope = rememberCoroutineScope()
  var isExpanded by rememberSaveable { mutableStateOf(false) }

  val suggestions = remember { mutableStateOf(ExampleSuggestions) }


  LaunchedEffect(textFieldState.text) {
    if (textFieldState.text.isNotEmpty()) {
      delay(300)
      scope.launch(Dispatchers.IO) {
        try {
          val result = autoCompleteSearch(textFieldState.text.toString())

          val parsedSuggestions = parseYouTubeSuggestions(result)
          withContext(Dispatchers.Main) {
            suggestions.value = parsedSuggestions
          }
        } catch (e: Exception) {
          Log.e("SearchRoute", "Error fetching suggestions", e)
        }
      }
    }
  }


  val inputField = @Composable {
    SearchBarDefaults.InputField(
      modifier = Modifier.fillMaxWidth(),
      state = textFieldState,
      onSearch = { isExpanded = false },
      expanded = isExpanded,
      onExpandedChange = { isExpanded = it },
      placeholder = { Text("Hinted search text") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
      trailingIcon = {
        IconButton(onClick = {
          search()
          isExpanded = false
        }) {
          Icon(
            Icons.AutoMirrored.Filled.Send, contentDescription =
            null
          )
        }
      },
    )
  }





  Box(
    modifier
      .fillMaxSize()
      .padding(8.dp),
  ) {
    DockedSearchBar(modifier = Modifier
      .fillMaxWidth()
      .zIndex(10f),
      inputField = inputField,
      onExpandedChange = { expanded ->
        isExpanded = expanded
      },
      expanded = isExpanded,
      content = {
        Column(Modifier.verticalScroll(rememberScrollState())) {
          suggestions.value.forEach { suggestion ->


            ListItem(headlineContent = { Text(suggestion) },
              colors = ListItemDefaults.colors(containerColor = Color.Transparent),
              leadingContent = { Icon(Icons.Default.Search, contentDescription = null) },
              modifier = Modifier
                .clickable {
                  textFieldState.setTextAndPlaceCursorAtEnd(suggestion)
                  isExpanded = false
                  search()
                }
                .fillMaxWidth())

          }

        }

      })


    Container(
      Modifier
        .fillMaxWidth()
        .padding(top = 64.dp)
    ) {
      Column(Modifier.fillMaxSize()) {
//RESULTS WILL GO HERE
      }

    }


  }
}

private fun search() {
  TODO()
}