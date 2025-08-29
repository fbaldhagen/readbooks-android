package com.fbaldhagen.readbooks.ui.toc

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fbaldhagen.readbooks.domain.model.TocItem
import com.fbaldhagen.readbooks.ui.components.shimmerBackground
import org.readium.r2.shared.publication.Href

@Composable
fun TableOfContentsScreen(
    viewModel: TableOfContentsViewModel = hiltViewModel(),
    onConfigureDetailTitle: (String) -> Unit,
    onItemClick: (href: Href) -> Unit = { },
    contentPadding: PaddingValues
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.bookTitle) {
        val title = state.bookTitle
        if (title.isNotBlank()) {
            onConfigureDetailTitle(title)
        } else {
            onConfigureDetailTitle("Table of Contents")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> {
                TocListPlaceholder()
            }
            state.error != null -> {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            state.tocItems.isEmpty() -> {
                Text(
                    text = "Table of Contents is not available for this book.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                TableOfContentsList(
                    items = state.tocItems,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
private fun TableOfContentsList(
    items: List<TocItem>,
    onItemClick: (href: Href) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(items, key = { it.href.toString() }) { item ->
            TocRow(item = item, onClick = { onItemClick(item.href) })
        }
    }
}

@Composable
private fun TocRow(
    item: TocItem,
    onClick: () -> Unit
) {
    val indentation = (item.level * 24).dp

    val fontWeight = if (item.level == 0) FontWeight.Bold else FontWeight.Normal

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                top = 12.dp,
                bottom = 12.dp,
                start = 16.dp + indentation,
                end = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = fontWeight
        )
    }
}

@Composable
private fun TocListPlaceholder() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) {
        items(15) { index ->
            val indentation = when {
                index % 5 == 1 -> 24.dp
                index % 5 == 2 -> 48.dp
                else -> 0.dp
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 12.dp, bottom = 12.dp,
                        start = 16.dp + indentation, end = 16.dp
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = (1f - (index % 5 * 0.1f)))
                        .height(20.dp)
                        .shimmerBackground()
                )
            }
        }
    }
}