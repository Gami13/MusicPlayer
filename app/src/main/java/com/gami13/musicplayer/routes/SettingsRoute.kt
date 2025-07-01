package com.gami13.musicplayer.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gami13.musicplayer.R
import com.gami13.musicplayer.composables.Container
import com.gami13.musicplayer.composables.Previewer
import com.gami13.musicplayer.locales.LocaleCode
import com.gami13.musicplayer.locales.formatName
import com.gami13.musicplayer.viewmodels.SettingsUiState
import com.gami13.musicplayer.viewmodels.SettingsViewModel
import kotlinx.coroutines.flow.flowOf


@Preview(showBackground = true, uiMode = 0x21)
@Composable
fun SettingsRoutePreview() {
  val uiState = SettingsUiState(
    selectedLocale = LocaleCode.EN_US,
    isLanguageDropdownExpanded = false,
    languageDropdownValue = "English",
    musicDirectoryUri = "",
    musicDirectoryUriPretty = "",
    availableLanguages = LocaleCode.entries.sortedBy { it.formatName() }

  )
  Previewer {
    SettingsRouteContent(
      uiState = uiState,
      onLanguageDropdownExpandedChanged = {},
      onLanguageSelected = {},
      onBrowseDirectoryClicked = {}
    )
  }
}

@Composable
fun SettingsRoute(
  viewModel: SettingsViewModel = viewModel()
) {
  val uiState by viewModel.uiState.collectAsState()

  SettingsRouteContent(
    uiState = uiState,
    onLanguageDropdownExpandedChanged = viewModel::onLanguageDropdownExpandedChanged,
    onLanguageSelected = viewModel::onLanguageSelected,
    onBrowseDirectoryClicked = viewModel::onBrowseDirectoryClicked
  )
}


@Composable
fun SettingsRouteContent(
  uiState: SettingsUiState = SettingsUiState(),
  onLanguageDropdownExpandedChanged: (Boolean) -> Unit = {},
  onLanguageSelected: (LocaleCode) -> Unit = {},
  onBrowseDirectoryClicked: () -> Unit = {}

) {
  RouteWrapper {
    Column {
      Container(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

          Text(
            text = stringResource(R.string.general_settings),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
          )

          LanguageSettings(
            uiState = uiState,
            onLanguageDropdownExpandedChanged = onLanguageDropdownExpandedChanged,
            onLanguageSelected = onLanguageSelected
          )

          MusicDirectory(
            uiState = uiState,
            onBrowseDirectoryClicked = onBrowseDirectoryClicked
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSettings(
  uiState: SettingsUiState = SettingsUiState(),
  onLanguageDropdownExpandedChanged: (Boolean) -> Unit = {},
  onLanguageSelected: (LocaleCode) -> Unit = {}
) {
  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Top
  ) {
    Body(
      header = stringResource(R.string.language),
      stringResource(R.string.settings_language_description)
    )

    ExposedDropdownMenuBox(
      expanded = uiState.isLanguageDropdownExpanded,
      onExpandedChange = onLanguageDropdownExpandedChanged
    ) {
      TextField(
        readOnly = true,
        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
        value = uiState.languageDropdownValue,
        onValueChange = { /* No-op since it's read-only */ },
        label = { Text(stringResource(R.string.select)) },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isLanguageDropdownExpanded) },
      )

      ExposedDropdownMenu(
        expanded = uiState.isLanguageDropdownExpanded,
        onDismissRequest = { onLanguageDropdownExpandedChanged(false) },
      ) {
        uiState.availableLanguages.forEach { locale ->
          DropdownMenuItem(
            text = { Text(locale.formatName()) },
            onClick = { onLanguageSelected(locale) },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
          )
        }
      }
    }
  }
}

@Composable
private fun MusicDirectory(
  uiState: SettingsUiState = SettingsUiState(),
  onBrowseDirectoryClicked: () -> Unit = {}
) {
  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Top
  ) {
    Body(
      stringResource(R.string.music_directory),
      stringResource(R.string.music_directory_description)
    )

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-2).dp)
      ) {
        Button(onClick = onBrowseDirectoryClicked) {
          Icon(Icons.Default.Folder, contentDescription = null)
          Text(text = stringResource(R.string.browse))
        }
        Text(
          text = uiState.musicDirectoryUriPretty,
          fontSize = 10.sp
        )
      }
    }
  }
}

@Composable
private fun Body(header: String, description: String) {
  Column(Modifier.fillMaxWidth(0.55f).padding(end = 8.dp)) {
    Text(text = header, style = MaterialTheme.typography.titleMedium)
    Text(
      style = MaterialTheme.typography.bodySmall,
      text = description,

      )
  }
}