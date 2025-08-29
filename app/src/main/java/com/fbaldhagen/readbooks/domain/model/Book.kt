package com.fbaldhagen.readbooks.domain.model

data class Book(
    val id: Long = 0,
    val remoteId: String? = null,
    val title: String,
    val author: String?,
    val description: String?,
    val filePath: String,
    val coverImagePath: String?,
    val dateAdded: Long,
    val lastReadLocator: String? = null,
    val lastOpenedTimestamp: Long? = null,
    val rating: Int = 0,
    val readingStatus: ReadingStatus = ReadingStatus.NOT_STARTED
)