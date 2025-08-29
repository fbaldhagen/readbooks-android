package com.fbaldhagen.readbooks.ui.reader

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.readium.r2.shared.publication.Href
import org.readium.r2.shared.publication.Link

private fun List<Link>.flatten(): List<Pair<Link, Int>> {
    val flattenedList = mutableListOf<Pair<Link, Int>>()
    fun traverse(links: List<Link>, depth: Int) {
        for (link in links) {
            flattenedList.add(Pair(link, depth))
            traverse(link.children, depth + 1)
        }
    }
    traverse(this, 0)
    return flattenedList
}

@Composable
fun TableOfContentsScreen(
    tableOfContents: List<Link>,
    onChapterClicked: (Href) -> Unit,
    modifier: Modifier = Modifier
) {
    val flattenedToc = tableOfContents.flatten()

    Column(modifier = modifier) {
        Text(
            text = "Table of Contents",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        HorizontalDivider()
        LazyColumn {
            items(flattenedToc) { (link, depth) ->
                TableOfContentsItem(
                    link = link,
                    depth = depth,
                    onClick = { onChapterClicked(link.href) }
                )
            }
        }
    }
}

@Composable
private fun TableOfContentsItem(
    link: Link,
    depth: Int,
    onClick: () -> Unit
) {
    val paddingStart = (16 + (depth * 16)).dp

    Text(
        text = link.title ?: "Untitled Chapter",
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(start = paddingStart)
    )
}