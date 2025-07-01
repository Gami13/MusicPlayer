package com.gami13.musicplayer.viewmodels

import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gami13.musicplayer.MainActivity
import com.gami13.musicplayer.SettingsRepository
import com.gami13.musicplayer.locales.LocaleCode
import com.gami13.musicplayer.locales.formatName
import com.gami13.musicplayer.locales.new
import com.gami13.musicplayer.locales.toListCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsUiState(
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

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        initializeSettings()
        observeMusicDirectory()
    }

    private fun initializeSettings() {
        val currentLocale = LocaleCode.new(LocaleList.getDefault()[0].toLanguageTag())
        _uiState.value = _uiState.value.copy(
            selectedLocale = currentLocale,
            languageDropdownValue = currentLocale.formatName()
        )
    }

    private fun observeMusicDirectory() {
        viewModelScope.launch {
            settingsRepository.musicDirectory.collect { directoryUri ->
                _uiState.value = _uiState.value.copy(
                    musicDirectoryUri = directoryUri,
                    musicDirectoryUriPretty = directoryUri.split(":").last()
                )
            }
        }
    }

    fun onLanguageDropdownExpandedChanged(expanded: Boolean) {
        _uiState.value = _uiState.value.copy(
            isLanguageDropdownExpanded = expanded
        )
    }

    fun onLanguageSelected(locale: LocaleCode) {
        _uiState.value = _uiState.value.copy(
            selectedLocale = locale,
            languageDropdownValue = locale.formatName(),
            isLanguageDropdownExpanded = false
        )

        AppCompatDelegate.setApplicationLocales(locale.toListCompat())
    }

    fun onBrowseDirectoryClicked() {
        MainActivity.openDocumentTree.launch(null)
    }

}
