package com.fbaldhagen.readbooks.ui.reader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.fbaldhagen.readbooks.domain.model.Bookmark
import org.readium.r2.shared.publication.Href
import org.readium.r2.shared.publication.Link
import org.readium.r2.shared.publication.Locator

@Composable
fun ReaderDetailsScreen(
    tableOfContents: List<Link>,
    bookmarks: List<Bookmark>,
    onChapterClicked: (Href) -> Unit,
    onBookmarkClicked: (Locator) -> Unit,
    onDeleteBookmark: (Bookmark) -> Unit,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Contents", "Bookmarks")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> TableOfContentsScreen(
                tableOfContents = tableOfContents,
                onChapterClicked = onChapterClicked
            )
            1 -> BookmarksScreen(
                bookmarks = bookmarks,
                onBookmarkClicked = onBookmarkClicked,
                onDeleteBookmark = onDeleteBookmark
            )
        }
    }
}