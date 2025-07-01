package com.gami13.musicplayer.routes.search

import androidx.lifecycle.ViewModel
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.SettingsRepository
import com.gami13.musicplayer.locales.LocaleCode
import com.gami13.musicplayer.locales.formatName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SearchUIState(
    val selectedLocale: LocaleCode = LocaleCode.EN_US,
    val isLanguageDropdownExpanded: Boolean = false,
    val languageDropdownValue: String = "",
    val musicDirectoryUri: String = "",
    val musicDirectoryUriPretty: String = "",
    val availableLanguages: List<LocaleCode> = LocaleCode.entries.sortedBy { it.formatName() }
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository = MainActivity.settingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUIState())
    val uiState: StateFlow<SearchUIState> = _uiState.asStateFlow()

    init {
        initializeSettings()
    }

    private fun initializeSettings() {

    }


}
