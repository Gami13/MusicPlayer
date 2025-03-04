package com.gami13.musicplayer.routes

import android.content.Intent
import android.os.LocaleList
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.os.LocaleListCompat
import com.gami13.musicplayer.Container
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.locales.LocaleCode
import com.gami13.musicplayer.locales.formatName
import com.gami13.musicplayer.locales.new
import com.gami13.musicplayer.locales.toListCompat

private val CategoryHeaderSize = 24.sp
private val CategoryHeaderWeight = FontWeight.Bold

private data class Settings(
  var language: LocaleListCompat = LocaleListCompat.getDefault()
)

private val desiredSettings = Settings()

@Preview(showBackground = true, uiMode = 0x21)
@Composable
fun SettingsRoute(modifier: Modifier = Modifier) {
  Log.d("test", "SettingsRoute")


  MainActivity.FAB = @Composable {
    FloatingActionButton(onClick = {
      AppCompatDelegate.setApplicationLocales(desiredSettings.language)

    }) {
      Icon(Icons.Default.Save, "Save")
    }
  }

  Column(
    modifier
      .fillMaxSize()
      .padding(8.dp)
  ) {
    Container(Modifier.fillMaxWidth()) {
      Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

        Text(
          text = "General Settings",
          fontSize = CategoryHeaderSize,
          fontWeight = CategoryHeaderWeight,
          color = MaterialTheme.colorScheme.primary
        )
        LanguageSettings()
        MusicDirectory()

      }

    }


  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSettings() {
//TODO: Add language settings not just design
  var isExpanded by remember { mutableStateOf(false) }
  val currentLocale = LocaleCode.new(LocaleList.getDefault()[0].toLanguageTag())
  var selectedLocale by remember { mutableStateOf(currentLocale) }
  var value by remember { mutableStateOf(currentLocale.formatName()) }

  val languageList = LocaleCode.entries.sortedBy { it.formatName() }



  Row(
    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top
  ) {
    Body(header = "Language", "Changes the language used in the application's interface")

    ExposedDropdownMenuBox(


      expanded = isExpanded, onExpandedChange = {
        isExpanded = !isExpanded
      }) {

      TextField(
        readOnly = true,
        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
        value = value,
        onValueChange = { newValue ->
          value = newValue
        },
        label = { Text("Select") },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },

        )
      ExposedDropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { isExpanded = false },
      ) {
        languageList.forEach { item ->
          DropdownMenuItem(
            text = { Text((item.formatName())) },
            onClick = {
              value = item.formatName()
              selectedLocale = item
              isExpanded = false
              desiredSettings.language = item.toListCompat()
            },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
          )
        }
      }

    }

  }
}


@Composable
private fun MusicDirectory() {
//TODO: Add directory settings not just design
  val localContext = LocalContext.current

  Row(
    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top
  ) {


    Body(
      "Music directory",
      "Sets the directory the application will use for storing the music files",
    )

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-2).dp)
      ) {

        Button(
          onClick = {

//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
//              addFlags(
//                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
//                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
//                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//              )
//            }
//TODO
//            MainActivity().openDocumentTree

          },
        ) {
          Icon(Icons.Default.Folder, contentDescription = null)
          Text(text = "Browse")
        }
        Text("No directory selected", fontSize = 10.sp)
      }
    }

  }
}

@Composable
private fun Body(header: String, description: String) {
  Column(Modifier.fillMaxWidth(0.55f)) {
    Text(text = header, fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp)
    Text(
      text = description, fontSize = 12.sp, lineHeight = 14.sp
    )
  }
}