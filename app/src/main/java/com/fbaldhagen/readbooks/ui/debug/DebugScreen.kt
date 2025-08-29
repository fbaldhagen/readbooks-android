package com.fbaldhagen.readbooks.ui.debug

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DebugScreen(
    viewModel: DebugViewModel = hiltViewModel(),
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Developer Options", style = MaterialTheme.typography.headlineSmall)

        // Bookworm Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Bookworm", style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.addBookwormProgress(1) }) { Text("+1 Book") }
                    Button(onClick = { viewModel.addBookwormProgress(9) }) { Text("+9 Books") }
                }
            }
        }

        // Page Turner Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Page Turner", style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.addPageTurnerProgress(1) }) { Text("+1 Hour") }
                    Button(onClick = { viewModel.addPageTurnerProgress(40) }) { Text("+40 Hours") }
                }
            }
        }

        // Night Owl Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Night Owl", style = MaterialTheme.typography.titleLarge)
                Button(onClick = { viewModel.triggerNightOwlProgress() }) { Text("Trigger Night Owl") }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Reset Card
        OutlinedButton(
            onClick = { viewModel.resetAchievements() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Reset All Achievements")
        }
    }
}