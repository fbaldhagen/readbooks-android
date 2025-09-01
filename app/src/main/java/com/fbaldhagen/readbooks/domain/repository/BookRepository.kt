package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.data.model.BookEntity
import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.model.FilterState
import com.fbaldhagen.readbooks.domain.model.LibraryBook
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.model.SortType
import kotlinx.coroutines.flow.Flow
import org.readium.r2.shared.publication.Publication

interface BookRepository {

    suspend fun openBook(filePath: String): Result<Publication>

    fun getLibraryBooks(
        query: String,
        sortType: SortType,
        filters: FilterState,
        showArchived: Boolean = false
    ): Flow<List<LibraryBook>>

    suspend fun addBookToLibrary(filePath: String): Result<Long>

    suspend fun seedLibraryFromAssets(): Result<Unit>

    suspend fun forceUpdateAssets(): Result<Unit>

    suspend fun saveReadingProgress(bookId: Long, locator: String?)

    suspend fun getBookById(bookId: Long): BookEntity?

    suspend fun updateLastOpenedTimestamp(bookId: Long)

    fun getRecentlyReadBooks(): Flow<List<LibraryBook>>

    fun getBookDetails(bookId: Long): Flow<BookDetails>

    suspend fun updateRating(bookId: Long, rating: Int)

    fun getOtherBooksByAuthor(author: String, currentBookId: Long): Flow<List<LibraryBook>>

    fun getAllReadingTimestamps(): Flow<List<Long>>

    fun getFinishedBookCount(): Flow<Int>

    suspend fun updateReadingStatus(bookId: Long, status: ReadingStatus)

    fun getInProgressBooksFlow(): Flow<List<LibraryBook>>

    suspend fun resetReadingProgress(bookId: Long)

    suspend fun addBook(book: Book)

    fun isBookInLibrary(remoteId: Int): Flow<Boolean>

    suspend fun getRemoteIdForLocalId(localId: Long): String?

    suspend fun getBookByRemoteId(remoteId: String): BookDetails?

    suspend fun addBookToLibrary(
        filePath: String,
        remoteId: String? = null,
        title: String? = null,
        author: String? = null
    ): Result<Long>

    suspend fun addRemoteBookToLibrary(
        filePath: String,
        remoteId: String? = null,
        title: String? = null,
        author: String? = null,
        description: String? = null
    ): Result<Long>

    fun getBookStreamByRemoteId(remoteId: String): Flow<BookDetails?>

    fun getTotalBookCount(): Flow<Int>

    suspend fun toggleArchiveStatus(bookId: Long)
}