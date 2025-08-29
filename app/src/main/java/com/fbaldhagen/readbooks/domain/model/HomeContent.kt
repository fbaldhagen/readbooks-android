package com.fbaldhagen.readbooks.domain.model

data class HomeContent(
    val currentlyReading: LibraryBook?,
    val recentBooks: List<LibraryBook>
)