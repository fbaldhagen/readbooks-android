package com.fbaldhagen.readbooks.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.domain.model.AppTheme
import com.fbaldhagen.readbooks.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings = settingsRepository.readerSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = com.fbaldhagen.readbooks.domain.model.ReaderSettings()
        )

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.updateTheme(theme)
        }
    }

    fun setFontSize(sizePercent: Int) {
        viewModelScope.launch {
            settingsRepository.updateFontSize(sizePercent)
        }
    }

    fun setPagePadding(paddingDp: Int) {
        viewModelScope.launch {
            settingsRepository.updatePagePadding(paddingDp)
        }
    }
}