package com.fbaldhagen.readbooks.ui.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberDismissState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwipeToDismiss
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissValue
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fbaldhagen.readbooks.domain.model.Bookmark
import org.readium.r2.shared.publication.Locator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookmarksScreen(
    bookmarks: List<Bookmark>,
    onBookmarkClicked: (Locator) -> Unit,
    onDeleteBookmark: (Bookmark) -> Unit,
    modifier: Modifier = Modifier
) {
    if (bookmarks.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No bookmarks yet.", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }


    LazyColumn(modifier = modifier) {
        items(
            items = bookmarks,
            key = { it.id }
        ) { bookmark ->
            val dismissState = rememberDismissState(
                confirmStateChange = {
                    if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                        onDeleteBookmark(bookmark)
                        return@rememberDismissState true
                    }
                    false
                }
            )

            SwipeToDismiss(
                state = dismissState,
                background = {
                    val color = MaterialTheme.colorScheme.errorContainer
                    val iconColor = MaterialTheme.colorScheme.onErrorContainer
                    Box(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).background(color),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = iconColor)
                    }
                },
                dismissContent = {
                    BookmarkItem(
                        bookmark = bookmark,
                        onClick = { onBookmarkClicked(bookmark.locator) }
                    )
                }
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun BookmarkItem(bookmark: Bookmark, onClick: () -> Unit) {

    val textSnippet = remember(bookmark.locator.text) {
        val before = bookmark.locator.text.before?.trim()?.takeLast(50) ?: ""
        val highlight = bookmark.locator.text.highlight?.trim() ?: ""
        val after = bookmark.locator.text.after?.trim()?.take(50) ?: ""
        "...$before$highlight$after..."
    }

    val formattedDate = remember(bookmark.creationDate) {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(bookmark.creationDate)
    }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = bookmark.locator.title ?: "Untitled Chapter",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = textSnippet,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}