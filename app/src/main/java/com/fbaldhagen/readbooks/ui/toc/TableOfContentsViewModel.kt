package com.fbaldhagen.readbooks.ui.toc

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.domain.model.TocItem
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.readium.r2.shared.publication.Link
import javax.inject.Inject

@HiltViewModel
class TableOfContentsViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(TableOfContentsState())
    val state = _state.asStateFlow()

    init {
        val bookId: Long? = savedStateHandle["bookId"]
        if (bookId != null) {
            loadTableOfContents(bookId)
        } else {
            _state.update { it.copy(isLoading = false, error = "Book not found.") }
        }
    }

    private fun loadTableOfContents(bookId: Long) = viewModelScope.launch {
        val bookEntity = bookRepository.getBookById(bookId)
        if (bookEntity == null) {
            _state.update { it.copy(isLoading = false, error = "Could not load book data.") }
            return@launch
        }

        bookRepository.openBook(bookEntity.filePath)
            .onSuccess { publication ->
                val toc = publication.tableOfContents
                val flattenedToc = flattenToc(toc)
                _state.update {
                    it.copy(
                        isLoading = false,
                        tocItems = flattenedToc,
                        bookTitle = publication.metadata.title!!
                    )
                }
            }
            .onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.localizedMessage ?: "Failed to open book."
                    )
                }
            }
    }

    private fun flattenToc(links: List<Link>, level: Int = 0): List<TocItem> {
        return links.flatMap { link ->
            val children = flattenToc(link.children, level + 1)
            val item = TocItem(
                title = link.title ?: "Unknown Chapter",
                href = link.href,
                level = level
            )
            listOf(item) + children
        }
    }
}