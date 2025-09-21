package com.fbaldhagen.readbooks.ui.bookdetails

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.model.LibraryBook
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.ui.common.TopBarState
import com.fbaldhagen.readbooks.ui.components.BookCoverItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    viewModel: BookDetailsViewModel = hiltViewModel(),
    onConfigureTopBar: (TopBarState) -> Unit,
    onNavigateBack: () -> Unit,
    onTocClick: (bookId: Long) -> Unit,
    onReadClick: (bookId: Long) -> Unit,
    onBookClick: (Long) -> Unit,
    contentPadding: PaddingValues
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BookDetailsEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    LaunchedEffect(state.book) {
        val book = state.book
        val title = book?.title?.takeIf { it.isNotBlank() } ?: "Book Details"
        val isLocalBook = book?.localId != null

        val topBarActions: @Composable RowScope.() -> Unit = if (isLocalBook) {
            {
                IconButton(onClick = { showBottomSheet = true }) {
                    Icon(Icons.Default.MoreVert, "More options")
                }
            }
        } else {
            {}
        }

        onConfigureTopBar(
            TopBarState.Detail(
                title = title,
                actions = topBarActions
            )
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to permanently delete this book and all its data?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteBook()
                    showDeleteDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            state.book?.takeIf { it.localId != null }?.let { book ->
                ManageBookBottomSheet(
                    currentStatus = book.readingStatus,
                    onDismiss = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showBottomSheet = false
                        }
                    },
                    onStatusChange = viewModel::updateReadingStatus,
                    onResetProgress = viewModel::resetReadingProgress,
                    onDeleteBook = { showDeleteDialog = true },
                    onTocClick = {
                        onTocClick(book.localId!!)
                    }
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            state.error != null -> {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
            state.book != null -> {
                val book = state.book!!
                val isLocalBook = book.localId != null

                BookDetailsContent(
                    book = book,
                    isLocalBook = isLocalBook,
                    downloadState = state.downloadState,
                    moreByAuthor = state.moreByAuthor,
                    onReadClick = { onReadClick(book.localId!!) },
                    onListenClick = viewModel::onListenClicked,
                    onDownloadClick = viewModel::onDownloadClicked,
                    onBookClick = onBookClick,
                    onRatingChanged = viewModel::onRatingChanged,
                    onCancelClick = viewModel::onCancelClicked
                )
            }
        }
    }
}

@Composable
private fun ManageBookBottomSheet(
    currentStatus: ReadingStatus,
    onDismiss: () -> Unit,
    onStatusChange: (ReadingStatus) -> Unit,
    onResetProgress: () -> Unit,
    onDeleteBook: () -> Unit,
    onTocClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Manage Book",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        Text(
            text = "SET STATUS",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )

        StatusOptionRow(
            text = "In Progress",
            isSelected = currentStatus == ReadingStatus.IN_PROGRESS,
            onClick = { onStatusChange(ReadingStatus.IN_PROGRESS) }
        )
        StatusOptionRow(
            text = "Finished",
            isSelected = currentStatus == ReadingStatus.FINISHED,
            onClick = { onStatusChange(ReadingStatus.FINISHED) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        ActionItem(
            icon = Icons.AutoMirrored.Filled.MenuBook,
            text = "Table of Contents",
            onClick = {
                onTocClick()
                onDismiss()
            }
        )
        ActionItem(
            icon = Icons.Default.Refresh,
            text = "Reset Reading Progress",
            onClick = {
                onResetProgress()
                onDismiss()
            }
        )
        ActionItem(
            icon = Icons.Default.Delete,
            text = "Delete Book",
            isDestructive = true,
            onClick = {
                onDeleteBook()
                onDismiss()
            }
        )
    }
}

@Composable
private fun StatusOptionRow(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = onClick)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ActionItem(
    icon: ImageVector,
    text: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val textColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = textColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge, color = textColor)
    }
}

@Composable
private fun BookDetailsContent(
    book: BookDetails,
    isLocalBook: Boolean,
    downloadState: DownloadState,
    moreByAuthor: List<LibraryBook>,
    onReadClick: () -> Unit,
    onListenClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onBookClick: (Long) -> Unit,
    onRatingChanged: (Int) -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 16.dp + navBarPadding.calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Card(
                    modifier = Modifier
                        .width(120.dp)
                        .aspectRatio(2f / 3f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    val imageModel = book.coverImagePath ?: book.coverImageUrl
                    AsyncImage(
                        model = imageModel,
                        contentDescription = "Cover for ${book.title}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    if (book.author != null) {
                        Text(
                            text = "by ${book.author}",
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Italic
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isLocalBook) {
                        StarRatingBar(
                            modifier = Modifier.padding(top = 8.dp),
                            currentRating = book.rating,
                            onRatingChanged = onRatingChanged
                        )
                    }

                }
            }
        }

        item {
            ActionButton(
                isLocalBook = isLocalBook,
                downloadState = downloadState,
                book = book,
                onReadClick = onReadClick,
                onListenClick = onListenClick,
                onDownloadClick = onDownloadClick,
                onCancelClick = onCancelClick
            )
        }

        if (isLocalBook && book.readingStatus == ReadingStatus.IN_PROGRESS && book.readingProgress != null) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    LinearProgressIndicator(
                        progress = { book.readingProgress },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your Progress",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (!book.description.isNullOrBlank()) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Synopsis",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = book.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (moreByAuthor.isNotEmpty()) {
            item {
                Text(
                    text = "More by ${book.author ?: "this Author"}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
                    textAlign = TextAlign.Start,
                )
                MoreByAuthorShelf(books = moreByAuthor, onBookClick = onBookClick)
            }
        }
    }
}

@Composable
private fun ActionButton(
    isLocalBook: Boolean,
    downloadState: DownloadState,
    book: BookDetails,
    onReadClick: () -> Unit,
    onListenClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val buttonState = remember(isLocalBook, downloadState) {
        if (!isLocalBook) downloadState::class else book.readingStatus::class
    }

    AnimatedContent(
        targetState = buttonState,
        label = "ActionButtonAnimation",
        transitionSpec = {
            fadeIn(animationSpec = tween(220, delayMillis = 90))
                .togetherWith(fadeOut(animationSpec = tween(90)))
        }
    ) { state ->
        when (state) {
            DownloadState.NotDownloaded::class -> {
                Button(onClick = onDownloadClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Add to Library")
                }
            }

            DownloadState.InProgress::class -> {
                val progress = (downloadState as? DownloadState.InProgress)?.progress ?: 0f
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth().height(ButtonDefaults.MinHeight),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier
                            .matchParentSize()
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                shape = ButtonDefaults.outlinedShape
                            )
                            .fillMaxWidth(progress)
                        )
                        Text(text = "Downloading... ${(progress * 100).toInt()}%")
                    }
                }
            }

            DownloadState.Failed::class -> {
                Button(
                    onClick = onDownloadClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Download Failed. Retry?")
                }
            }

            else -> {
                val buttonText = when (book.readingStatus) {
                    ReadingStatus.FINISHED -> "Read Again"
                    ReadingStatus.IN_PROGRESS -> {
                        val progress = book.readingProgress
                        if (progress != null && progress > 0f) {
                            "Continue Reading (${(progress * 100).toInt()}%)"
                        } else {
                            "Continue Reading"
                        }
                    }
                    ReadingStatus.NOT_STARTED -> "Read"
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onReadClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = buttonText)
                    }

                    OutlinedButton(
                        onClick = onListenClick,
                        modifier = Modifier.size(56.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen to this book"
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MoreByAuthorShelf(
    books: List<LibraryBook>,
    onBookClick: (Long) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(books.size) { index ->
            val book = books[index]
            BookCoverItem(
                book = book,
                onClick = { onBookClick(book.id) },
                modifier = Modifier.width(120.dp)
            )
        }
    }
}

@Composable
fun StarRatingBar(
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    currentRating: Int,
    onRatingChanged: (Int) -> Unit,
    filledColor: Color = MaterialTheme.colorScheme.primary,
    hollowColor: Color = MaterialTheme.colorScheme.outline
) {
    Row(modifier = modifier) {
        for (starNumber in 1..maxStars) {
            val isSelected = starNumber <= currentRating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline
            val iconTintColor = if (isSelected) filledColor else hollowColor

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onRatingChanged(starNumber)
                    }
                    .size(40.dp)
            )
        }
    }
}