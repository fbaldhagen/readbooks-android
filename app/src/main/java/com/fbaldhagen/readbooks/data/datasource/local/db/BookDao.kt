package com.fbaldhagen.readbooks.data.datasource.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fbaldhagen.readbooks.data.model.BookEntity
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBook(book: BookEntity): Long

    @Query("UPDATE books SET lastReadLocator = :locator WHERE id = :bookId")
    suspend fun updateLastReadLocator(bookId: Long, locator: String?)

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: Long): BookEntity?

    @Query("SELECT filePath FROM books WHERE id = :bookId")
    suspend fun getBookFilePath(bookId: Long): String?

    @Query("UPDATE books SET lastOpenedTimestamp = :timestamp WHERE id = :bookId")
    suspend fun updateLastOpenedTimestamp(bookId: Long, timestamp: Long)

    @Query("UPDATE books SET isArchived = :isArchived WHERE id = :bookId")
    suspend fun setArchiveStatus(bookId: Long, isArchived: Boolean)

    @Query("SELECT * FROM books WHERE lastOpenedTimestamp IS NOT NULL ORDER BY lastOpenedTimestamp DESC")
    fun getRecentlyReadBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :bookId")
    fun getBookStream(bookId: Long): Flow<BookEntity>

    @Query("UPDATE books SET rating = :rating WHERE id = :bookId")
    suspend fun updateRating(bookId: Long, rating: Int)

    @Query("SELECT * FROM books WHERE author = :author AND id != :currentBookId ORDER BY title ASC")
    fun getOtherBooksByAuthor(author: String, currentBookId: Long): Flow<List<BookEntity>>

    @Query("SELECT lastOpenedTimestamp FROM books WHERE lastOpenedTimestamp IS NOT NULL")
    fun getAllTimestamps(): Flow<List<Long>>

    @Query("UPDATE books SET readingStatus = :status WHERE id = :bookId")
    suspend fun updateReadingStatus(bookId: Long, status: ReadingStatus)

    @Query("SELECT COUNT(*) FROM books WHERE readingStatus = 'FINISHED'")
    fun getFinishedBookCount(): Flow<Int>

    @Query("UPDATE books SET readingStatus = 'FINISHED', lastReadLocator = NULL WHERE id = :bookId")
    suspend fun markBookAsFinished(bookId: Long)

    @Query("UPDATE books SET readingStatus = 'NOT_STARTED', lastReadLocator = NULL WHERE id = :bookId")
    suspend fun markBookAsNotStarted(bookId: Long)

    @Query("SELECT readingStatus FROM books WHERE id = :bookId")
    suspend fun getReadingStatus(bookId: Long): ReadingStatus?

    @Query("UPDATE books SET readingStatus = 'IN_PROGRESS', lastReadLocator = :locator, lastOpenedTimestamp = :timestamp WHERE id = :bookId")
    suspend fun startReadingBook(bookId: Long, locator: String, timestamp: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM books WHERE remoteId = :remoteId LIMIT 1)")
    fun isBookInLibrary(remoteId: Int): Flow<Boolean>

    @Query("SELECT remoteId FROM books WHERE id = :localId")
    suspend fun getRemoteIdForLocalId(localId: Long): String?

    @Query("SELECT * FROM books WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getBookByRemoteId(remoteId: String): BookEntity?

    @Query("SELECT * FROM books WHERE remoteId = :remoteId LIMIT 1")
    fun getBookStreamByRemoteId(remoteId: String): Flow<BookEntity?>

    @Query("SELECT COUNT(*) FROM books")
    fun getTotalBookCount(): Flow<Int>

    @Query("""
        SELECT * FROM books 
        WHERE readingStatus = 'IN_PROGRESS' 
        ORDER BY lastOpenedTimestamp DESC
    """)
    fun getBooksInProgress(): Flow<List<BookEntity>>

    @Query("""
        SELECT * FROM books
        WHERE
            (:query = '' OR title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%')
        AND
            (:statuses IS NULL OR readingStatus IN (:statuses))
        AND isArchived = :isArchived    
        ORDER BY
            CASE :sortType
                WHEN 'TITLE_ASC' THEN title
                WHEN 'AUTHOR_ASC' THEN author
            END ASC,
            CASE :sortType
                WHEN 'DATE_ADDED_DESC' THEN dateAdded
                WHEN 'LAST_READ_DESC' THEN lastOpenedTimestamp
            END DESC
    """)
    fun getFilteredSortedBooks(
        query: String,
        sortType: String,
        statuses: Set<String>?,
        isArchived: Boolean
    ): Flow<List<BookEntity>>

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: Long)
}