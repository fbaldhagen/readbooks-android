package com.fbaldhagen.readbooks.ui.discover

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.ui.components.placeholders.BookCarouselPlaceholder
import com.fbaldhagen.readbooks.ui.components.placeholders.BookCoverPlaceholder

@Composable
fun DiscoverScreenPlaceholder() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        userScrollEnabled = false
    ) {
        items(3) {
            BookCarouselPlaceholder()
        }
    }
}

@Composable
fun SearchGridPlaceholder() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 110.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false
    ) {
        items(12) {
            BookCoverPlaceholder(showText = true)
        }
    }
}