package com.fbaldhagen.readbooks.ui.discover

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.ui.common.TopBarState
import com.fbaldhagen.readbooks.ui.components.PagingErrorState
import com.fbaldhagen.readbooks.ui.components.placeholders.BookCarouselPlaceholder
import com.fbaldhagen.readbooks.ui.home.DiscoverCoverItem
import com.fbaldhagen.readbooks.utils.mapThrowableToUserMessage
import kotlinx.coroutines.flow.Flow

@Composable
fun DiscoverScreen(
    onConfigureTopBar: (TopBarState) -> Unit,
    contentPadding: PaddingValues,
    onBookClick: (String) -> Unit
) {
    val viewModel: DiscoverViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.searchQuery) {
        onConfigureTopBar(
            TopBarState.Search(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                onClose = viewModel::onSearchClosed,
                hint = "Search all books..."
            )
        )
    }

    AnimatedContent(
        targetState = state.content,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        },
        label = "DiscoverContentAnimation",
        modifier = Modifier.padding(contentPadding)
    ) { contentState ->
        DiscoverContent(
            content = contentState,
            getBooksForTopic = viewModel::getBooksForTopic,
            onBookClick = onBookClick
        )
    }
}

@Composable
fun DiscoverContent(
    content: DiscoverContent,
    getBooksForTopic: (String) -> Flow<PagingData<DiscoverBook>>,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (content) {
        is DiscoverContent.Loading -> {
            DiscoverScreenPlaceholder()
        }
        is DiscoverContent.Browsing -> {
            BrowseContent(
                sections = content.sections,
                getBooksForTopic = getBooksForTopic,
                onBookClick = onBookClick,
                modifier = modifier
            )
        }
        is DiscoverContent.Searching -> {
            SearchContent(
                searchResults = content.searchResults,
                onBookClick = onBookClick,
                modifier = modifier
            )
        }
    }
}

@Composable
fun BrowseContent(
    sections: List<DiscoverSectionInfo>,
    getBooksForTopic: (String) -> Flow<PagingData<DiscoverBook>>,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(items = sections, key = { it.topic }) { sectionInfo ->
            BookCarousel(
                title = sectionInfo.title,
                booksFlow = getBooksForTopic(sectionInfo.topic),
                onBookClick = onBookClick
            )
        }
    }
}

@Composable
fun BookCarousel(
    title: String,
    booksFlow: Flow<PagingData<DiscoverBook>>,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val books = booksFlow.collectAsLazyPagingItems()

    Column(modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(12.dp))

        when (val state = books.loadState.refresh) {
            is LoadState.Loading -> {
                BookCarouselPlaceholder()
            }
            is LoadState.Error -> {
                PagingErrorState(
                    errorMessage = mapThrowableToUserMessage(state.error),
                    onRetry = { books.retry() }
                )
            }
            is LoadState.NotLoading -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        count = books.itemCount,
                        key = books.itemKey { it.id }
                    ) { index ->
                        books[index]?.let { book ->
                            DiscoverCoverItem(
                                book = book,
                                onClick = { onBookClick(book.id.toString()) },
                                modifier = Modifier.width(110.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchContent(
    searchResults: Flow<PagingData<DiscoverBook>>,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val books = searchResults.collectAsLazyPagingItems()

    when (val state = books.loadState.refresh) {
        is LoadState.Loading -> {
            SearchGridPlaceholder()
        }
        is LoadState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                PagingErrorState(
                    errorMessage = mapThrowableToUserMessage(state.error),
                    onRetry = { books.retry() }
                )
            }
        }
        else -> {
            if (books.itemCount == 0) {
                Text("No results found.")
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 110.dp),
                    modifier = modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        count = books.itemCount,
                        key = books.itemKey { it.id }
                    ) { index ->
                        books[index]?.let { book ->
                            DiscoverCoverItem(
                                book = book,
                                onClick = { onBookClick(book.id.toString()) }
                            )
                        }
                    }
                }
            }
        }
    }
}
