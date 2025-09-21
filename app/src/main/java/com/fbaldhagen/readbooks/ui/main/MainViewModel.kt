package com.fbaldhagen.readbooks.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.fbaldhagen.readbooks.domain.controller.TtsController
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TtsMiniPlayerUiState(
    val isVisible: Boolean = false,
    val title: String = "",
    val author: String = "",
    val isPlaying: Boolean = false,
    val progress: Float = 0f
)

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @androidx.annotation.OptIn(UnstableApi::class)
@Inject constructor(
    private val ttsController: TtsController,
    private val bookRepository: BookRepository
) : ViewModel() {

    init {
        Log.d("TTS_DEBUG", "MainViewModel created, observing TtsController: ${ttsController.hashCode()}")
    }

    val uiState: StateFlow<TtsMiniPlayerUiState> =
        ttsController.currentBookId.flatMapLatest { bookId ->
            if (bookId == null) {
                flowOf(TtsMiniPlayerUiState(isVisible = false))
            } else {
                combine(
                    bookRepository.getBookDetails(bookId),
                    ttsController.isPlaying,
                    ttsController.currentLocator
                ) { bookDetails, isPlaying, locator ->
                    val progress = locator?.locations?.totalProgression?.toFloat() ?: (bookDetails.readingProgress ?: 0f)

                    TtsMiniPlayerUiState(
                        isVisible = true,
                        title = bookDetails.title,
                        author = bookDetails.author ?: "Unknown Author",
                        isPlaying = isPlaying,
                        progress = progress
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TtsMiniPlayerUiState()
        )


    fun onPlayPause() {
        ttsController.togglePlayPause()
    }

    fun onClose() {
        ttsController.stop()
    }

    fun onNext() {
        ttsController.next()
    }

    fun onPrevious() {
        ttsController.previous()
    }
}