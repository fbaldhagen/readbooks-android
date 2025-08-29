package com.fbaldhagen.readbooks.domain.model

/**
 * Represents the full details of a single book for the details screen.
 */
data class BookDetails(
    // Common properties
    val remoteId: String?, // The Gutendex ID.
    val title: String,
    val author: String?,
    val description: String?,
    val coverImageUrl: String?, // URL from the remote API,
    val epubUrl: String?,

    // Properties for local library books (nullable)
    val localId: Long? = null,
    val coverImagePath: String? = null, // Local file path for the cover
    val dateAdded: Long? = null,
    val lastReadLocator: String? = null,
    val readingProgress: Float? = null,
    val rating: Int = 0,
    val readingStatus: ReadingStatus = ReadingStatus.NOT_STARTED
)