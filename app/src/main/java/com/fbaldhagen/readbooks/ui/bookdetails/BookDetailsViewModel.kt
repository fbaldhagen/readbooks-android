package com.fbaldhagen.readbooks.ui.bookdetails

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.fbaldhagen.readbooks.Screen
import com.fbaldhagen.readbooks.data.worker.DownloadWorker
import com.fbaldhagen.readbooks.domain.controller.TtsController
import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.model.LibraryBook
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.usecase.DeleteBookUseCase
import com.fbaldhagen.readbooks.domain.usecase.DownloadBookUseCase
import com.fbaldhagen.readbooks.domain.usecase.GetBookDetailsUseCase
import com.fbaldhagen.readbooks.domain.usecase.GetOtherBooksByAuthorUseCase
import com.fbaldhagen.readbooks.domain.usecase.GetPublicationUseCase
import com.fbaldhagen.readbooks.domain.usecase.ResetReadingProgressUseCase
import com.fbaldhagen.readbooks.domain.usecase.UpdateBookRatingUseCase
import com.fbaldhagen.readbooks.domain.usecase.UpdateReadingStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.util.mediatype.MediaType
import java.util.UUID
import javax.inject.Inject

@androidx.annotation.OptIn(UnstableApi::class)
@HiltViewModel
class BookDetailsViewModel @androidx.annotation.OptIn(UnstableApi::class)
@Inject constructor(
    private val updateBookRatingUseCase: UpdateBookRatingUseCase,
    private val getOtherBooksByAuthorUseCase: GetOtherBooksByAuthorUseCase,
    private val updateReadingStatusUseCase: UpdateReadingStatusUseCase,
    private val resetReadingProgressUseCase: ResetReadingProgressUseCase,
    private val getBookDetailsUseCase: GetBookDetailsUseCase,
    private val downloadBookUseCase: DownloadBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    private val workManager: WorkManager,
    private val savedStateHandle: SavedStateHandle,
    private val getPublicationUseCase: GetPublicationUseCase,
    private val ttsController: TtsController
) : ViewModel() {
    init {
        Log.d("TTS_DEBUG", "BookDetailsViewModel created, observing TtsController: ${ttsController.hashCode()}")
    }

    private val localIdFlow: StateFlow<Long> = savedStateHandle.getStateFlow(Screen.LOCAL_ID_ARG, 0L)
    private val remoteIdFlow: StateFlow<String?> = savedStateHandle.getStateFlow(Screen.REMOTE_ID_ARG, null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val bookDetailsResultFlow: Flow<Result<BookDetails>> =
        combine(localIdFlow, remoteIdFlow) { localId, remoteId ->
            Pair(localId.takeIf { it != 0L }, remoteId)
        }.flatMapLatest { (localId, remoteId) ->
            getBookDetailsUseCase(localId = localId, remoteId = remoteId)
        }

    private var activeWorkId: UUID? = null
    private val downloadStateFlow = MutableStateFlow<DownloadState>(DownloadState.NotDownloaded)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val moreByAuthorFlow: Flow<List<LibraryBook>> =
        bookDetailsResultFlow
            .map { it.getOrNull() }
            .flatMapLatest { book ->
                val author = book?.author
                if (book?.localId != null && !author.isNullOrBlank()) {
                    getOtherBooksByAuthorUseCase(author, book.localId)
                } else {
                    flowOf(emptyList())
                }
            }

    val state: StateFlow<BookDetailsState> =
        combine(
            bookDetailsResultFlow,
            moreByAuthorFlow,
            downloadStateFlow
        ) { bookResult, moreByAuthor, downloadState ->
            bookResult.fold(
                onSuccess = { book ->
                    val currentDownloadState = if (book.localId != null) {
                        DownloadState.Completed
                    } else {
                        downloadState
                    }
                    BookDetailsState(
                        isLoading = false,
                        book = book,
                        moreByAuthor = moreByAuthor,
                        downloadState = currentDownloadState,
                        error = null
                    )
                },
                onFailure = { error ->
                    BookDetailsState(
                        isLoading = false,
                        error = error.localizedMessage ?: "An error occurred"
                    )
                }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BookDetailsState(isLoading = true)
        )

    private val _events = MutableSharedFlow<BookDetailsEvent>()
    val events = _events.asSharedFlow()

    fun onRatingChanged(rating: Int) {
        val currentBook = state.value.book ?: return
        val localId = currentBook.localId ?: return
        val newRating = if (currentBook.rating == rating) 0 else rating

        viewModelScope.launch {
            updateBookRatingUseCase(localId, newRating)
        }
    }

    fun updateReadingStatus(status: ReadingStatus) {
        val localId = state.value.book?.localId ?: return
        viewModelScope.launch {
            updateReadingStatusUseCase(localId, status)
        }
    }

    fun resetReadingProgress() {
        val localId = state.value.book?.localId ?: return
        viewModelScope.launch {
            resetReadingProgressUseCase(localId)
        }
    }


    fun onListenClicked() {
        viewModelScope.launch {
            val book = state.value.book ?: return@launch
            val localId = book.localId ?: return@launch
            val filePath = book.epubUrl ?: return@launch

            getPublicationUseCase(filePath).fold(
                onSuccess = { publication ->
                    val lastReadLocatorJson = book.lastReadLocator

                    val initialLocator: Locator? = if (!lastReadLocatorJson.isNullOrBlank()) {
                        try {
                            val thinLocator = Locator.fromJSON(JSONObject(lastReadLocatorJson)) ?: return@launch

                            val link = publication.linkWithHref(thinLocator.href)
                            if (link != null) {
                                Locator(
                                    href = link.href.resolve(),
                                    mediaType = link.mediaType ?: MediaType.XHTML,
                                    title = link.title,
                                    locations = thinLocator.locations,
                                    text = thinLocator.text
                                )
                            } else {
                                Log.w("BookDetailsViewModel", "Could not find link for href: ${thinLocator.href}")
                                null
                            }
                        } catch (e: Exception) {
                            Log.e("BookDetailsViewModel", "Failed to parse or resolve locator JSON", e)
                            null
                        }
                    } else {
                        null
                    }

                    ttsController.play(bookId = localId, initialLocator = initialLocator)
                },
                onFailure = { error ->
                    Log.e("BookDetailsViewModel", "Failed to get publication for TTS", error)
                }
            )
        }
    }

    fun deleteBook() {
        val localId = state.value.book?.localId ?: return

        viewModelScope.launch {
            try {
                deleteBookUseCase(localId)
                _events.emit(BookDetailsEvent.NavigateBack)
            } catch (e: Exception) {
                Log.e("YourBookViewModel", "Error deleting book", e)
            }
        }
    }

    fun onDownloadClicked() {
        val book = state.value.book ?: return
        val remoteId = book.remoteId ?: return
        val epubUrl = book.epubUrl ?: return

        val workId = downloadBookUseCase(
            remoteId = remoteId,
            epubUrl = epubUrl,
            title = book.title,
            author = book.author,
            description = book.description
        )
        activeWorkId = workId

        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(workId).collect { workInfo ->
                handleWorkInfo(workInfo)
            }
        }
    }

    fun onCancelClicked() {
        activeWorkId?.let {
            workManager.cancelWorkById(it)
            activeWorkId = null
        }
    }

    private fun handleWorkInfo(workInfo: WorkInfo?) {
        if (workInfo == null) return

        when (workInfo.state) {
            WorkInfo.State.RUNNING -> {
                val progress = workInfo.progress.getInt(DownloadWorker.KEY_PROGRESS, 0)
                downloadStateFlow.value = DownloadState.InProgress(progress / 100f)
            }
            WorkInfo.State.SUCCEEDED -> {
                val newLocalId = workInfo.outputData.getLong(DownloadWorker.KEY_LOCAL_ID, 0L)
                if (newLocalId != 0L) {
                    savedStateHandle[Screen.LOCAL_ID_ARG] = newLocalId
                }
                downloadStateFlow.value = DownloadState.Completed
                activeWorkId = null
            }
            WorkInfo.State.FAILED -> {
                downloadStateFlow.value = DownloadState.Failed("Download failed.")
                activeWorkId = null
            }
            WorkInfo.State.CANCELLED -> {
                downloadStateFlow.value = DownloadState.NotDownloaded
                activeWorkId = null
            }
            else -> {}
        }
    }
}

sealed interface BookDetailsEvent {
    data object NavigateBack : BookDetailsEvent
}