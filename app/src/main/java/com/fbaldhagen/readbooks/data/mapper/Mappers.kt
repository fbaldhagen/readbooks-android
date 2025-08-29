package com.fbaldhagen.readbooks.data.mapper

import com.fbaldhagen.readbooks.data.model.BookEntity
import com.fbaldhagen.readbooks.data.model.remote.BookDetailDto
import com.fbaldhagen.readbooks.data.model.remote.BookDto
import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.model.LibraryBook

fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = this.id,
        remoteId = this.remoteId,
        title = this.title,
        author = this.author,
        description = this.description,
        filePath = this.filePath,
        coverImagePath = this.coverImagePath,
        dateAdded = this.dateAdded,
        lastReadLocator = this.lastReadLocator,
        lastOpenedTimestamp = this.lastOpenedTimestamp,
        rating = this.rating,
        readingStatus = this.readingStatus
    )
}

fun BookEntity.toDomain(): Book {
    return Book(
        id = this.id,
        remoteId = this.remoteId,
        title = this.title,
        author = this.author,
        description = this.description,
        filePath = this.filePath,
        coverImagePath = this.coverImagePath,
        dateAdded = this.dateAdded,
        lastReadLocator = this.lastReadLocator,
        lastOpenedTimestamp = this.lastOpenedTimestamp,
        rating = this.rating,
        readingStatus = this.readingStatus
    )
}

fun BookEntity.toBookDetails(): BookDetails {
    return BookDetails(
        localId = this.id,
        remoteId = this.remoteId,
        title = this.title,
        author = this.author,
        description = this.description,
        coverImagePath = this.coverImagePath,
        dateAdded = this.dateAdded,
        lastReadLocator = this.lastReadLocator,
        rating = this.rating,
        readingStatus = this.readingStatus,
        coverImageUrl = null,
        epubUrl = null,
        readingProgress = null
    )
}

fun BookEntity.toLibraryBook(readingProgress: Float?): LibraryBook {
    return LibraryBook(
        id = this.id,
        remoteId = this.remoteId,
        title = this.title,
        author = this.author,
        coverImagePath = this.coverImagePath,
        filePath = this.filePath,
        dateAdded = this.dateAdded,
        readingProgress = readingProgress,
        readingStatus = this.readingStatus
    )
}


fun BookDto.toDomain(): DiscoverBook {
    return DiscoverBook(
        id = this.id,
        title = this.title,
        author = this.authors.firstOrNull()?.name.formatAuthorName(),
        coverUrl = this.formats.imageUrl ?: ""
    )
}

fun BookDetailDto.toBookDetails(): BookDetails {
    val description = this.summaries.firstOrNull()
        ?: if (this.subjects.isNotEmpty()) this.subjects.joinToString(", ") else null

    return BookDetails(
        remoteId = this.id.toString(),
        title = this.title,
        author = this.authors.firstOrNull()?.name.formatAuthorName(),
        description = description,
        coverImageUrl = this.formats.imageUrl,
        epubUrl = this.formats.epubUrl
    )
}

fun String?.formatAuthorName(): String? {
    if (this.isNullOrBlank()) {
        return this
    }

    val parts = this.split(',').map { it.trim() }
    return if (parts.size == 2) {
        "${parts[1]} ${parts[0]}"
    } else {
        this
    }
}