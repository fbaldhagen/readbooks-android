package com.fbaldhagen.readbooks.ui.home
import com.fbaldhagen.readbooks.domain.model.LibraryBook

data class HomeState(
    val isLoading: Boolean = true,
    val currentlyReadingBook: LibraryBook? = null,
    val recentlyReadBooks: List<LibraryBook>? = null,
    val error: String? = null,
    val greeting: String = "",
    val jumpBackInHeader: String = "",
    val discoverHeader: String = ""
)