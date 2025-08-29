package com.fbaldhagen.readbooks.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.AppTheme
import com.fbaldhagen.readbooks.domain.model.ReaderSettings

@Composable
fun ReaderSettingsContent(
    settings: ReaderSettings,
    onThemeChange: (AppTheme) -> Unit,
    onFontSizeChange: (Int) -> Unit,
    onPaddingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ThemeSelector(
            selectedTheme = settings.theme,
            onThemeSelected = onThemeChange
        )
        FontSizeSelector(
            fontSizePercent = settings.fontSizePercent,
            onFontSizeChange = onFontSizeChange
        )
        PagePaddingSelector(
            paddingDp = settings.pagePaddingDp,
            onPaddingChange = onPaddingChange
        )
    }
}

@Composable
private fun ThemeSelector(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Theme", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            AppTheme.entries.forEach { theme ->
                OutlinedButton(
                    onClick = { onThemeSelected(theme) },
                    colors = if (theme == selectedTheme) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(theme.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }
    }
}

@Composable
private fun FontSizeSelector(
    fontSizePercent: Int,
    onFontSizeChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Font Size", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("A", style = MaterialTheme.typography.bodySmall)
            Slider(
                value = fontSizePercent.toFloat(),
                onValueChange = { onFontSizeChange(it.toInt()) },
                valueRange = 80f..200f,
                steps = 11,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            )
            Text("A", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun PagePaddingSelector(
    paddingDp: Int,
    onPaddingChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Page Padding (Top & Bottom)", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = paddingDp.toFloat(),
            onValueChange = { onPaddingChange(it.toInt()) },
            valueRange = 0f..64f,
            steps = 7,
        )
    }
}