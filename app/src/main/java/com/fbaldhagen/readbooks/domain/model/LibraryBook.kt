package com.fbaldhagen.readbooks.domain.model

data class LibraryBook(
    val id: Long,
    val remoteId: String?,
    val title: String,
    val author: String?,
    val filePath: String,
    val coverImagePath: String?,
    val dateAdded: Long,
    val readingProgress: Float? = null,
    val readingStatus: ReadingStatus = ReadingStatus.NOT_STARTED
)