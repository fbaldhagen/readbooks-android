package com.fbaldhagen.readbooks.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.fbaldhagen.readbooks.R
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.model.LibraryBook
import com.fbaldhagen.readbooks.ui.common.TopBarBackground
import com.fbaldhagen.readbooks.ui.common.TopBarState
import com.fbaldhagen.readbooks.ui.components.BookCoverItem
import com.fbaldhagen.readbooks.ui.components.PagingErrorState
import com.fbaldhagen.readbooks.ui.components.shimmerBackground
import com.fbaldhagen.readbooks.utils.mapThrowableToUserMessage

@Composable
fun HomeScreen(
    onBookClick: (bookId: Long) -> Unit,
    onDiscoverBookClick: (remoteId: String) -> Unit,
    onConfigureTopBar: (TopBarState) -> Unit,
    contentPadding: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val discoverBooks = viewModel.discoverBooks.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        onConfigureTopBar(
            TopBarState.Standard(
                title = "",
                background = TopBarBackground.Scrim
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HomeContent(
            state = state,
            onBookClick = onBookClick,
            discoverBooks = discoverBooks,
            onDiscoverBookClick = onDiscoverBookClick,
            contentPadding = contentPadding
        )
    }
}

@Composable
fun HomeContent(
    state: HomeState,
    onBookClick: (bookId: Long) -> Unit,
    discoverBooks: LazyPagingItems<DiscoverBook>,
    onDiscoverBookClick: (remoteId: String) -> Unit,
    contentPadding: PaddingValues
) {
    val isLocalContentLoading = state.recentlyReadBooks == null

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            if (isLocalContentLoading) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(32.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .shimmerBackground()
                )
            } else {
                Text(
                    text = state.greeting,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        if (isLocalContentLoading) {
            item {
                CurrentlyReadingHeroPlaceholder(modifier = Modifier.padding(horizontal = 16.dp))
            }
        } else {
            state.currentlyReadingBook?.let { book ->
                item {
                    CurrentlyReadingHeroCard(
                        book = book,
                        onClick = { onBookClick(book.id) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        if (isLocalContentLoading) {
            item {
                BookCarouselPlaceholder(modifier = Modifier.padding(horizontal = 16.dp))
            }
        } else {
            state.recentlyReadBooks?.takeIf { it.isNotEmpty() }?.let { books ->
                item {
                    BookCarousel(
                        title = state.jumpBackInHeader,
                        books = books,
                        onBookClick = onBookClick
                    )
                }
            }
        }

        if (state.error != null && !isLocalContentLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        item {
            DiscoverCarousel(
                title = state.discoverHeader,
                books = discoverBooks,
                onBookClick = onDiscoverBookClick
            )
        }
    }
}

@Composable
private fun BookCarousel(
    title: String,
    books: List<LibraryBook>,
    onBookClick: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(books, key = { it.id }) { book ->
                BookCoverItem(
                    book = book,
                    onClick = { onBookClick(book.id) },
                    modifier = Modifier.width(120.dp)
                )
            }
        }
    }
}

@Composable
fun CurrentlyReadingHeroCard(
    book: LibraryBook,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = book.coverImagePath,
                placeholder = painterResource(id = R.drawable.ic_placeholder_book),
                error = painterResource(id = R.drawable.ic_placeholder_book),
                contentDescription = "Cover for ${book.title}",
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    book.author?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1
                        )
                    }
                }
                Column {
                    val progress = book.readingProgress ?: 0f
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "You're at ${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun DiscoverCarousel(
    title: String,
    books: LazyPagingItems<DiscoverBook>,
    onBookClick: (remoteId: String) -> Unit
) {
    val loadState = books.loadState.refresh

    when (loadState) {
        is LoadState.Loading -> {
            DiscoverCarouselPlaceholder()
        }
        is LoadState.Error -> {
            val errorState = books.loadState.refresh as LoadState.Error
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PagingErrorState(
                        errorMessage = mapThrowableToUserMessage(errorState.error),
                        onRetry = books::retry
                    )
                }
            }
        }
        else -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        count = books.itemCount,
                        key = books.itemKey { it.id }
                    ) { index ->
                        val book = books[index]
                        if (book != null) {
                            DiscoverCoverItem(
                                book = book,
                                onClick = { onBookClick(book.id.toString()) },
                                modifier = Modifier.width(120.dp)
                            )
                        } else {
                            //
                        }
                    }

                    if (books.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier.size(120.dp, 180.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiscoverCoverItem(
    book: DiscoverBook,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = "Cover for ${book.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_placeholder_book),
                error = painterResource(id = R.drawable.ic_placeholder_book),
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                book.author?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}