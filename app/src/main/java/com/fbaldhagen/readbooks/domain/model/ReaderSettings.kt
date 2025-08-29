package com.fbaldhagen.readbooks.domain.model

enum class AppTheme {
    LIGHT, SEPIA, DARK
}

data class ReaderSettings(
    val theme: AppTheme = AppTheme.LIGHT,
    val fontSizePercent: Int = 100,
    val pagePaddingDp: Int = 24
)