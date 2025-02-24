package com.gami13.musicplayer.routes

import android.os.LocaleList
import android.util.Log
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
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gami13.musicplayer.Container
import com.gami13.musicplayer.MainActivity

private val CategoryHeaderSize = 24.sp
private val CategoryHeaderWeight = FontWeight.Bold


@Preview(showBackground = true, uiMode = 0x21)
@Composable
fun SettingsRoute(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Container(Modifier.fillMaxWidth())
        {
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
    var value by remember { mutableStateOf("English") }
    val languageList = listOf("English", "Polish")


    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Body(header = "Language", "Changes the language used in the application's interface")

        ExposedDropdownMenuBox(


            expanded = isExpanded,
            onExpandedChange = {
                isExpanded = !isExpanded
            }
        ) {

            TextField(
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
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
                        text = { Text(item) },
                        onClick = {
                            value = item
                            isExpanded = false
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

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
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
                        val languageList = LocaleList.getDefault()

                        Log.d(
                            "test",
                            MainActivity.appContext.resources.configuration.locales.toString()
                        )
                        Log.d("test", languageList.toString())
                        Log.d("test", AppCompatDelegate.getApplicationLocales().toString())

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
            text = description,
            fontSize = 12.sp,
            lineHeight = 14.sp
        )
    }
}