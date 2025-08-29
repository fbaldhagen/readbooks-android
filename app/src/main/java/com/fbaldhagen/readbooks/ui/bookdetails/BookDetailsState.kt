package com.fbaldhagen.readbooks.ui.bookdetails

import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.model.LibraryBook

data class BookDetailsState(
    val isLoading: Boolean = true,
    val book: BookDetails? = null,
    val moreByAuthor: List<LibraryBook> = emptyList(),
    val downloadState: DownloadState = DownloadState.NotDownloaded,
    val error: String? = null
)

sealed class DownloadState {
    data object NotDownloaded : DownloadState()
    data class InProgress(val progress: Float) : DownloadState()
    data object Completed : DownloadState()
    data class Failed(val error: String) : DownloadState()
}