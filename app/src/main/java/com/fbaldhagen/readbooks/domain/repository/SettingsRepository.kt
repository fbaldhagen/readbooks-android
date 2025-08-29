package com.fbaldhagen.readbooks.domain.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.fbaldhagen.readbooks.domain.model.AppTheme
import com.fbaldhagen.readbooks.domain.model.ReaderSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val FONT_SIZE_PERCENT = intPreferencesKey("font_size_percent")
        val PAGE_PADDING_DP = intPreferencesKey("page_padding_dp")
    }

    val readerSettingsFlow: Flow<ReaderSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val theme = AppTheme.valueOf(
                preferences[PreferencesKeys.APP_THEME] ?: AppTheme.LIGHT.name
            )
            val fontSize = preferences[PreferencesKeys.FONT_SIZE_PERCENT] ?: 100
            val padding = preferences[PreferencesKeys.PAGE_PADDING_DP] ?: 24

            ReaderSettings(theme, fontSize, padding)
        }

    suspend fun updateTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = theme.name
        }
    }

    suspend fun updateFontSize(sizePercent: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_SIZE_PERCENT] = sizePercent
        }
    }

    suspend fun updatePagePadding(paddingDp: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PAGE_PADDING_DP] = paddingDp
        }
    }
}