package com.gami13.musicplayer.routes

import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.R
import com.gami13.musicplayer.composables.Container
import com.gami13.musicplayer.locales.LocaleCode
import com.gami13.musicplayer.locales.formatName
import com.gami13.musicplayer.locales.new
import com.gami13.musicplayer.locales.toListCompat
import kotlinx.coroutines.flow.flowOf


@Preview(showBackground = true, uiMode = 0x21)
@Composable
fun SettingsRoute(modifier: Modifier = Modifier) {


  Column(
    modifier
      .fillMaxSize()
      .padding(8.dp)
  ) {
    Container(Modifier.fillMaxWidth()) {
      Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {


        Text(
          text = stringResource(R.string.general_settings),
          style = MaterialTheme.typography.headlineLarge,
          color = MaterialTheme.colorScheme.primary
        )
        LanguageSettings()
        MusicDirectory()

      }

    }


  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun LanguageSettings() {
  var isExpanded by remember { mutableStateOf(false) }
  val currentLocale = LocaleCode.new(LocaleList.getDefault()[0].toLanguageTag())
  var selectedLocale by remember { mutableStateOf(currentLocale) }
  var value by remember { mutableStateOf(currentLocale.formatName()) }

  val languageList = LocaleCode.entries.sortedBy { it.formatName() }



  Row(
    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top
  ) {
    Body(header = stringResource(R.string.language),
      stringResource(R.string.changes_the_language_used_in_the_application_s_interface)
    )

    ExposedDropdownMenuBox(


      expanded = isExpanded, onExpandedChange = {
        isExpanded = !isExpanded
      }) {

      TextField(
        readOnly = true,
        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
        value = value,
        onValueChange = { newValue ->
          value = newValue
        },
        label = { Text(stringResource(R.string.select)) },
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
              
              AppCompatDelegate.setApplicationLocales(item.toListCompat())

            },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
          )
        }
      }

    }

  }
}

@Preview
@Composable
private fun MusicDirectory() {
  var selectedDirectory = if (LocalInspectionMode.current) {
    flowOf("content://com.android.externalstorage.documents/tree/primary:Music")
  } else {
    MainActivity.settingsRepository.musicDirectory
  }

  Row(
    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top
  ) {
    Body(
      stringResource(R.string.music_directory),
      stringResource(R.string.sets_the_directory_the_application_will_use_for_storing_the_music_files),
    )
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-2).dp)
      ) {

        Button(
          onClick = {
            MainActivity.openDocumentTree.launch(null)
            selectedDirectory = MainActivity.settingsRepository.musicDirectory
          },
        ) {
          Icon(Icons.Default.Folder, contentDescription = null)
          Text(text = stringResource(R.string.browse))
        }
        val directoryUri = selectedDirectory.collectAsState(initial = "").value
        Text(
          text = if (directoryUri.isEmpty()) {
            stringResource(R.string.no_directory_selected)
          } else {
            directoryUri.split(":").last()
          },
          fontSize = 10.sp
        )
      }
    }

  }
}

@Composable
private fun Body(header: String, description: String) {
  Column(Modifier.fillMaxWidth(0.55f)) {
    Text(text = header, style = MaterialTheme.typography.titleMedium)
    Text(
      style = MaterialTheme.typography.bodySmall,
      text = description,

      )
  }
}