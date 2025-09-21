package com.fbaldhagen.readbooks.ui.reader

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.fbaldhagen.readbooks.di.ApplicationScope
import com.fbaldhagen.readbooks.domain.controller.TtsController
import com.fbaldhagen.readbooks.domain.model.AppTheme
import com.fbaldhagen.readbooks.domain.model.Bookmark
import com.fbaldhagen.readbooks.domain.model.TtsPlaybackState
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import com.fbaldhagen.readbooks.domain.repository.SettingsRepository
import com.fbaldhagen.readbooks.domain.usecase.AddBookmarkUseCase
import com.fbaldhagen.readbooks.domain.usecase.DeleteBookmarkUseCase
import com.fbaldhagen.readbooks.domain.usecase.EndReadingSessionUseCase
import com.fbaldhagen.readbooks.domain.usecase.GetBookmarksForBookUseCase
import com.fbaldhagen.readbooks.domain.usecase.GetPublicationUseCase
import com.fbaldhagen.readbooks.domain.usecase.StartReadingSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.preferences.Theme
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Href
import org.readium.r2.shared.publication.Link
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getPublicationUseCase: GetPublicationUseCase,
    private val bookRepository: BookRepository,
    private val getBookmarksForBookUseCase: GetBookmarksForBookUseCase,
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
    private val settingsRepository: SettingsRepository,
    private val startReadingSessionUseCase: StartReadingSessionUseCase,
    private val endReadingSessionUseCase: EndReadingSessionUseCase,
    private val ttsController: TtsController,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    private val _state = MutableStateFlow(ReaderState())
    val state: StateFlow<ReaderState> = _state.asStateFlow()
    private val _events = MutableSharedFlow<ReaderEvent>()
    val events = _events.asSharedFlow()
    private var currentLocator: Locator? = null
    private val bookId: Long = savedStateHandle.get<Long>("bookId")!!

    init {
        loadBook()
        collectBookmarks(bookId)
        collectReaderSettings()
        updateBookmarkState()
        collectTtsState()
    }

    private fun collectTtsState() {
        combine(
            ttsController.isPlaying,
            ttsController.currentBookId
        ) { isPlaying, ttsBookId ->
            if (ttsBookId == bookId && isPlaying) {
                TtsPlaybackState.PLAYING
            } else {
                TtsPlaybackState.IDLE
            }
        }.onEach { playbackState ->
            _state.update { it.copy(ttsPlaybackState = playbackState) }
        }.launchIn(viewModelScope)
    }

    private fun loadBook() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val initialHrefString = savedStateHandle.get<String>(ReaderActivity.EXTRA_HREF)?.let { encodedHref ->
                URLDecoder.decode(encodedHref, StandardCharsets.UTF_8.toString())
            }
            val initialHref: Href? = initialHrefString?.let { Href(it) }

            bookRepository.updateLastOpenedTimestamp(bookId)

            val book = bookRepository.getBookById(bookId)
            if (book == null) {
                _state.update { it.copy(isLoading = false, error = "Book with ID $bookId not found.") }
                return@launch
            }

            getPublicationUseCase(book.filePath).fold(
                onSuccess = { publication ->
                    val initialLocator = determineInitialLocator(
                        publication,
                        initialHref,
                        book.lastReadLocator
                    )

                    currentLocator = initialLocator

                    val factory = EpubNavigatorFactory(publication)

                    _state.update {
                        it.copy(
                            isLoading = false,
                            navigatorFactory = factory,
                            publication = publication,
                            initialLocator = initialLocator,
                            tableOfContents = publication.tableOfContents
                        )
                    }
                    startReadingSessionUseCase(bookId)
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(isLoading = false, error = "Failed to open book: ${exception.message}")
                    }
                }
            )
        }
    }

    @OptIn(ExperimentalReadiumApi::class)
    private fun collectReaderSettings() {
        viewModelScope.launch {
            settingsRepository.readerSettingsFlow.collect { settings ->
                val epubPrefs = EpubPreferences(
                    fontSize = settings.fontSizePercent / 100.0,
                    theme = when (settings.theme) {
                        AppTheme.LIGHT -> Theme.LIGHT
                        AppTheme.SEPIA -> Theme.SEPIA
                        AppTheme.DARK -> Theme.DARK
                    }
                )

                _state.update { it.copy(preferences = epubPrefs) }
            }
        }
    }

    fun goToHref(href: Href) {
        val publication = state.value.publication ?: return

        publication.locatorFromLink(Link(href = href))?.let { locator ->
            viewModelScope.launch {
                _events.emit(ReaderEvent.GoTo(locator))
            }
        }
    }

    fun goToLocator(locator: Locator) {
        viewModelScope.launch {
            _events.emit(ReaderEvent.GoTo(locator))
        }
    }

    fun onLocationChanged(locator: Locator) {
        currentLocator = locator
        updateBookmarkState()
    }

    private fun collectBookmarks(bookId: Long) {
        getBookmarksForBookUseCase(bookId)
            .onEach { bookmarks ->
                _state.update { it.copy(bookmarks = bookmarks) }
                updateBookmarkState()
            }
            .launchIn(viewModelScope)
    }

    fun toggleBookmark() {
        if (state.value.isCurrentPageBookmarked) {
            removeBookmarkForCurrentPage()
        } else {
            addBookmark()
        }
    }

    private fun addBookmark() {
        val locator = currentLocator ?: return
        viewModelScope.launch {
            addBookmarkUseCase(bookId, locator)
            _events.emit(ReaderEvent.ShowToast("Bookmark added"))
        }
    }

    private fun removeBookmarkForCurrentPage() {
        val locator = currentLocator ?: return

        val bookmarkToRemove = state.value.bookmarks.find { bookmark ->
            bookmark.locator.href == locator.href &&
                    bookmark.locator.locations.position == locator.locations.position
        }

        if (bookmarkToRemove != null) {
            viewModelScope.launch {
                deleteBookmarkUseCase(bookmarkToRemove)
                _events.emit(ReaderEvent.ShowToast("Bookmark removed"))
            }
        }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            deleteBookmarkUseCase(bookmark)
            _events.emit(ReaderEvent.ShowToast("Bookmark removed"))
        }
    }

    private fun updateBookmarkState() {
        val locator = currentLocator
        val bookmarks = state.value.bookmarks

        val isBookmarked = if (locator == null) {
            false
        } else {
            bookmarks.any { bookmark ->
                bookmark.locator.href == locator.href &&
                        bookmark.locator.locations.position == locator.locations.position
            }
        }

        _state.update { it.copy(isCurrentPageBookmarked = isBookmarked) }
    }

    fun saveProgress() {
        val locatorToSave = currentLocator ?: return
        applicationScope.launch {
            try {
                bookRepository.saveReadingProgress(bookId, locatorToSave.toJSON().toString())
                endReadingSessionUseCase(bookId)
                Log.d(TAG, "Progress saved and session ended successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save progress or end session", e)
            }
        }
    }

    fun toggleSystemUi() {
        _state.update { it.copy(isSystemUiVisible = !it.isSystemUiVisible) }
    }

    private fun determineInitialLocator(
        publication: Publication,
        href: Href?,
        savedLocatorJson: String?
    ): Locator? {
        return when {
            href != null -> {
                Log.d(TAG, "Opening from Href.")
                publication.locatorFromLink(Link(href = href))
            }
            !savedLocatorJson.isNullOrBlank() -> {
                Log.d(TAG, "Opening from saved locator.")
                Locator.fromJSON(JSONObject(savedLocatorJson))
            }
            else -> {
                Log.d(TAG, "Opening from start of book.")
                null
            }
        }
    }

    fun onTtsPlayPauseClicked() {
        if (ttsController.currentBookId.value == bookId) {
            ttsController.togglePlayPause()
        } else {
            val locator = currentLocator ?: state.value.initialLocator ?: return
            viewModelScope.launch {
                try {
                    ttsController.play(bookId, locator)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to start TTS", e)
                    _events.emit(ReaderEvent.ShowToast("Unable to start read-aloud"))
                }
            }
        }
    }

    companion object {
        private const val TAG = "ReaderViewModel"
    }
}