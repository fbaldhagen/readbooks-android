package com.fbaldhagen.readbooks.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.fbaldhagen.readbooks.domain.model.AppTheme

// Light Theme
private val LightColors = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainer = LightSurfaceContainer,
    outline = LightOutline
)

// Dark Theme
private val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainer = DarkSurfaceContainer,
    outline = DarkOutline
)

// Sepia Theme
private val SepiaColors = lightColorScheme(
    primary = SepiaPrimary,
    onPrimary = SepiaOnPrimary,
    secondary = SepiaSecondary,
    onSecondary = SepiaOnSecondary,
    background = SepiaBackground,
    onBackground = SepiaOnBackground,
    surface = SepiaSurface,
    onSurface = SepiaOnSurface,
    surfaceVariant = SepiaSurfaceVariant,
    onSurfaceVariant = SepiaOnSurfaceVariant,
    surfaceContainer = SepiaSurfaceContainer,
    outline = SepiaOutline
)

@Composable
fun ReadBooksTheme(
    appTheme: AppTheme? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        appTheme != null -> when (appTheme) {
            AppTheme.LIGHT -> LightColors
            AppTheme.DARK -> DarkColors
            AppTheme.SEPIA -> SepiaColors
        }

        isSystemInDarkTheme() -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}