package com.fbaldhagen.readbooks.ui.library

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fbaldhagen.readbooks.R
import com.fbaldhagen.readbooks.domain.model.LibraryBook
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.model.SortType
import com.fbaldhagen.readbooks.ui.common.TopBarState
import com.fbaldhagen.readbooks.ui.components.shimmerBackground
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onConfigureTopBar: (TopBarState) -> Unit,
    onBookClick: (bookId: Long) -> Unit,
    onNavigateToDiscover: () -> Unit,
    contentPadding: PaddingValues,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(state.topBarState) {
        val finalTopBarState = when (val currentState = state.topBarState) {
            is TopBarState.Standard -> currentState.copy(
                actions = {
                    LibraryActions(
                        displayMode = state.displayMode,
                        isArchiveVisible = state.isArchiveVisible,
                        onToggleDisplayMode = viewModel::onToggleDisplayMode,
                        onSortClicked = viewModel::onSortClicked,
                        onFilterClicked = viewModel::onFilterClicked,
                        onSearchClicked = viewModel::onSearchOpened,
                        onShowArchiveClicked = viewModel::onToggleArchiveView
                    )
                }
            )
            else -> currentState
        }
        onConfigureTopBar(finalTopBarState)
    }

    BackHandler(enabled = state.topBarState is TopBarState.Search) {
        viewModel.onSearchClosed()
    }

    LibraryContent(
        state = state,
        onBookClick = onBookClick,
        onDiscoverClick = onNavigateToDiscover,
        onClearFilters = viewModel::onClearFilters,
        modifier = Modifier.padding(contentPadding),
        onPrimaryFilterChanged = viewModel::onPrimaryFilterChanged,
        onToggleArchive = viewModel::onToggleArchiveStatus
    )

    if (state.isSortSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = viewModel::onDismissSortSheet,
            sheetState = sheetState
        ) {
            SortOptionsSheet(
                activeSort = state.activeSort,
                onSortSelected = viewModel::onSortChanged
            )
        }
    }

    if (state.isFilterSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = viewModel::onDismissFilterSheet,
            sheetState = sheetState
        ) {
            FilterOptionsSheet(
                activeFilters = state.activeFilters,
                onFilterToggled = viewModel::onFilterToggled,
                onClearFilters = viewModel::onClearFilters
            )
        }
    }
}

@Composable
private fun LibraryActions(
    displayMode: DisplayMode,
    isArchiveVisible: Boolean,
    onToggleDisplayMode: () -> Unit,
    onSortClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    onShowArchiveClicked: () -> Unit
) {
    IconButton(onClick = onSearchClicked) {
        Icon(Icons.Default.Search, contentDescription = "Search Library")
    }

    val viewIcon = if (displayMode == DisplayMode.GRID) {
        Icons.AutoMirrored.Filled.ViewList
    } else {
        Icons.Default.GridView
    }
    IconButton(onClick = onToggleDisplayMode) {
        Icon(viewIcon, contentDescription = "Switch View")
    }
    IconButton(onClick = onSortClicked) {
        Icon(Icons.AutoMirrored.Filled.Sort, "Sort")
    }
    IconButton(onClick = onFilterClicked) {
        Icon(Icons.Default.FilterList, "Filter")
    }

    var menuExpanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { menuExpanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            val menuText = if (isArchiveVisible) "My Library" else "Archived Books"
            DropdownMenuItem(
                text = { Text(menuText) },
                onClick = {
                    onShowArchiveClicked()
                    menuExpanded = false
                }
            )
        }
    }
}

@Composable
private fun FilterChipRow(
    activeFilter: LibraryFilter,
    onFilterSelected: (LibraryFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = remember {
        listOf(
            LibraryFilter.All,
            LibraryFilter.ByStatus(ReadingStatus.NOT_STARTED),
            LibraryFilter.ByStatus(ReadingStatus.IN_PROGRESS),
            LibraryFilter.ByStatus(ReadingStatus.FINISHED)
        )
    }

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = filters, key = { it.toString() }) { filter ->
            FilterChip(
                selected = (filter == activeFilter),
                onClick = { onFilterSelected(filter) },
                label = { Text(text = filter.toDisplayName()) }
            )
        }
    }
}

@Composable
fun LibraryContent(
    state: LibraryState,
    onBookClick: (localId: Long) -> Unit,
    onDiscoverClick: () -> Unit,
    onClearFilters: () -> Unit,
    onPrimaryFilterChanged: (LibraryFilter) -> Unit,
    onToggleArchive: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (state.totalBookCountInLibrary > 0 && !state.isLoading && !state.isArchiveVisible) {
            FilterChipRow(
                activeFilter = state.activePrimaryFilter,
                onFilterSelected = onPrimaryFilterChanged,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            when {
                state.isLoading -> {
                    when (state.displayMode) {
                        DisplayMode.GRID -> LibraryGridPlaceholder()
                        DisplayMode.LIST -> LibraryListPlaceholder()
                    }
                }

                state.totalBookCountInLibrary == 0 -> {
                    LibraryEmptyState(onDiscoverClick = onDiscoverClick)
                }

                state.books.isEmpty() && !state.isLoading -> {
                    LibraryFilteredEmptyState(onClearFilters = onClearFilters)
                }

                else -> {
                    when (state.displayMode) {
                        DisplayMode.GRID -> LibraryGrid(
                            books = state.books,
                            onBookClick = onBookClick,
                            isArchiveView = state.isArchiveVisible,
                            onToggleArchive = onToggleArchive
                        )

                        DisplayMode.LIST -> LibraryList(
                            books = state.books,
                            onBookClick = onBookClick,
                            isArchiveView = state.isArchiveVisible,
                            onToggleArchive = onToggleArchive
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryGrid(
    books: List<LibraryBook>,
    onBookClick: (localId: Long) -> Unit,
    isArchiveView: Boolean,
    onToggleArchive: (Long) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(books, key = { it.id }) { book ->
            BookItemWithContextMenu(
                bookId = book.id,
                isArchived = isArchiveView,
                onClick = { onBookClick(book.id) },
                onToggleArchive = onToggleArchive
            ) {
                LibraryBookGridItem(
                    book = book
                )
            }
        }
    }
}

@Composable
fun LibraryBookGridItem(
    book: LibraryBook,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            AsyncImage(
                model = book.coverImagePath,
                contentDescription = "Cover for ${book.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_placeholder_book),
                error = painterResource(id = R.drawable.ic_placeholder_book),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
                    .padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
            ) {
                Column {
                    Text(
                        text = book.title,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    book.author?.let {
                        Text(
                            text = it,
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (book.readingProgress != null && book.readingProgress > 0f && book.readingProgress < 1f) {
                LinearProgressIndicator(
                    progress = { book.readingProgress },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
            }
        }
    }
}

@Composable
fun LibraryGridPlaceholder() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false
    ) {
        items(12) {
            Box(
                modifier = Modifier
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerBackground()
            )
        }
    }
}

@Composable
fun LibraryEmptyState(onDiscoverClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.LibraryBooks,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your library is waiting",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Books you download or add from the Discover tab will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onDiscoverClick) {
            Text("Discover Books")
        }
    }
}

@Composable
fun LibraryFilteredEmptyState(onClearFilters: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No books found",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Try adjusting your filters to find what you're looking for.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onClearFilters) {
            Text("Clear Filters")
        }
    }
}

@Composable
fun SortOptionsSheet(
    activeSort: SortType,
    onSortSelected: (SortType) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Sort by",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        SortType.entries.forEach { sortType ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSortSelected(sortType) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = activeSort == sortType,
                    onClick = { onSortSelected(sortType) }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = sortType.name.replace("_", " ")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() })
            }
        }
    }
}

@Composable
fun FilterOptionsSheet(
    activeFilters: Set<ReadingStatus>,
    onFilterToggled: (ReadingStatus) -> Unit,
    onClearFilters: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filter by Status", style = MaterialTheme.typography.titleLarge)
            if (activeFilters.isNotEmpty()) {
                TextButton(onClick = onClearFilters) {
                    Text("Clear All")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ReadingStatus.entries.forEach { status ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFilterToggled(status) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = status in activeFilters,
                    onCheckedChange = { onFilterToggled(status) }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = status.name.replace("_", " ")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() })
            }
        }
    }
}

@Composable
fun LibraryBookListItem(
    book: LibraryBook,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = book.coverImagePath,
                contentDescription = "Cover for ${book.title}",
                modifier = Modifier
                    .width(80.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_placeholder_book),
                error = painterResource(id = R.drawable.ic_placeholder_book),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                book.author?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                BookProgress(progress = book.readingProgress)
            }
        }
    }
}

@Composable
private fun BookProgress(progress: Float?) {
    if (progress != null && progress > 0f) {
        val progressPercent = (progress * 100).toInt()
        Column {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(4.dp))
            val statusText = if (progress >= 1f) "Finished" else "$progressPercent% complete"
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun LibraryList(
    books: List<LibraryBook>,
    onBookClick: (localId: Long) -> Unit,
    isArchiveView: Boolean,
    onToggleArchive: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(books, key = { it.id }) { book ->
            BookItemWithContextMenu(
                bookId = book.id,
                isArchived = isArchiveView,
                onClick = { onBookClick(book.id) },
                onToggleArchive = onToggleArchive
            ) {
                LibraryBookListItem(
                    book = book
                )
            }
        }
    }
}

@Composable
fun LibraryListPlaceholder() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(8) {
            Row {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(8.dp))
                        .shimmerBackground()
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.fillMaxWidth().height(24.dp).shimmerBackground())
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth(0.6f).height(20.dp).shimmerBackground())
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(8.dp).shimmerBackground())
                }
            }
        }
    }
}

@Composable
private fun BookItemWithContextMenu(
    bookId: Long,
    isArchived: Boolean,
    onClick: () -> Unit,
    onToggleArchive: (Long) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isContextMenuVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = { isContextMenuVisible = true }
        )
    ) {
        content()

        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = { isContextMenuVisible = false }
        ) {
            val actionText = if (isArchived) "Unarchive" else "Archive"
            DropdownMenuItem(
                text = { Text(actionText) },
                onClick = {
                    onToggleArchive(bookId)
                    isContextMenuVisible = false
                }
            )
        }
    }
}