package com.fbaldhagen.readbooks.domain.model

data class CollectionWithBooks(
    val collection: Collection,
    val books: List<LibraryBook>
)