package com.fbaldhagen.readbooks.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.fbaldhagen.readbooks.data.datasource.local.db.BookDao
import com.fbaldhagen.readbooks.data.datasource.local.file.EpubDataSource
import com.fbaldhagen.readbooks.data.mapper.toBookDetails
import com.fbaldhagen.readbooks.data.mapper.toEntity
import com.fbaldhagen.readbooks.data.mapper.toLibraryBook
import com.fbaldhagen.readbooks.data.model.BookEntity
import com.fbaldhagen.readbooks.data.parser.LocatorParser
import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.model.FilterState
import com.fbaldhagen.readbooks.domain.model.LibraryBook
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.model.SortType
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.cover
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepositoryImpl @Inject constructor(
    private val epubDataSource: EpubDataSource,
    private val bookDao: BookDao,
    private val locatorParser: LocatorParser,
    @ApplicationContext private val context: Context
) : BookRepository {

    companion object {
        private const val ASSETS_VERSION_KEY = "assets_version"
        private const val CURRENT_ASSETS_VERSION = 1
    }

    private fun getSharedPrefs() = context.getSharedPreferences("book_assets", Context.MODE_PRIVATE)

    override fun getLibraryBooks(
        query: String,
        sortType: SortType,
        filters: FilterState,
        showArchived: Boolean
    ): Flow<List<LibraryBook>> {
        val statusNames: Set<String>? = if (filters.statuses.isNotEmpty()) {
            filters.statuses.map { it.name }.toSet()
        } else {
            null
        }

        return bookDao.getFilteredSortedBooks(
            query = query.trim(),
            sortType = sortType.name,
            statuses = statusNames,
            isArchived = showArchived
        ).map { entities ->
            entities.map { bookEntity ->
                val progress = locatorParser.parseTotalProgression(bookEntity.lastReadLocator)
                bookEntity.toLibraryBook(progress)
            }
        }
    }

    override suspend fun openBook(filePath: String): Result<Publication> {
        return epubDataSource.openEpub(filePath)
    }

    override suspend fun getBookFilePath(bookId: Long): String? {
        return bookDao.getBookFilePath(bookId)
    }

    override suspend fun addBookToLibrary(filePath: String): Result<Long> = withContext(Dispatchers.IO) {
        val publicationResult = epubDataSource.openEpub(filePath)

        publicationResult.fold(
            onSuccess = { publication ->
                try {
                    val coverPath = saveCover(publication)

                    val bookEntity = BookEntity(
                        title = publication.metadata.title!!,
                        author = publication.metadata.authors.firstOrNull()?.name,
                        filePath = filePath,
                        coverImagePath = coverPath,
                        description = publication.metadata.description,
                        dateAdded = System.currentTimeMillis()
                    )

                    val newId = bookDao.insertBook(bookEntity)
                    if (newId == -1L) {
                        Result.failure(Exception("Book already exists in the library."))
                    } else {
                        Result.success(newId)
                    }
                } catch (e: Exception) {
                    Result.failure(e)
                }
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }

    override suspend fun addBookToLibrary(
        filePath: String,
        remoteId: String?,
        title: String?,
        author: String?
    ): Result<Long> = withContext(Dispatchers.IO) {
        val publicationResult = epubDataSource.openEpub(filePath)

        publicationResult.fold(
            onSuccess = { publication ->
                try {
                    val coverPath = saveCover(publication)

                    val bookEntity = BookEntity(
                        title = title ?: publication.metadata.title ?: "Unknown Title",
                        author = author ?: publication.metadata.authors.firstOrNull()?.name,
                        remoteId = remoteId,
                        filePath = filePath,
                        coverImagePath = coverPath,
                        description = publication.metadata.description,
                        dateAdded = System.currentTimeMillis()
                    )

                    val newId = bookDao.insertBook(bookEntity)
                    if (newId == -1L) {
                        Result.failure(Exception("Book already exists in the library."))
                    } else {
                        Result.success(newId)
                    }
                } catch (e: Exception) {
                    Result.failure(e)
                }
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }

    private suspend fun saveCover(publication: Publication): String? {
        val coverBitmap = publication.cover() ?: return null

        return withContext(Dispatchers.IO) {
            val coversDir = File(context.filesDir, "covers")
            if (!coversDir.exists()) {
                coversDir.mkdirs()
            }

            val file = File(coversDir, "${UUID.randomUUID()}.png")
            file.outputStream().use {
                coverBitmap.compress(Bitmap.CompressFormat.PNG, 85, it)
            }
            file.absolutePath
        }
    }

    override suspend fun seedLibraryFromAssets(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val prefs = getSharedPrefs()
            val lastVersion = prefs.getInt(ASSETS_VERSION_KEY, 0)

            if (lastVersion == CURRENT_ASSETS_VERSION) {
                return@withContext Result.success(Unit)
            }

            val bookAssetsPath = "epub_books"
            val assetFiles = context.assets.list(bookAssetsPath) ?: return@withContext Result.success(Unit)

            val booksDir = File(context.filesDir, "books")
            if (!booksDir.exists()) {
                booksDir.mkdirs()
            }

            for (fileName in assetFiles) {
                if (fileName.endsWith(".epub")) {
                    val destinationFile = File(booksDir, fileName)

                    context.assets.open("$bookAssetsPath/$fileName").use { inputStream ->
                        destinationFile.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    addBookToLibrary(destinationFile.absolutePath)
                }
            }
            prefs.edit().putInt(ASSETS_VERSION_KEY, CURRENT_ASSETS_VERSION).apply()

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun forceUpdateAssets(): Result<Unit> = withContext(Dispatchers.IO) {
        getSharedPrefs().edit().putInt(ASSETS_VERSION_KEY, 0).apply()
        seedLibraryFromAssets()
    }

    override suspend fun saveReadingProgress(bookId: Long, locator: String?) {
        val validLocator = locator ?: return

        val currentStatus = bookDao.getReadingStatus(bookId)

        if (currentStatus == ReadingStatus.NOT_STARTED) {
            bookDao.startReadingBook(bookId, validLocator, System.currentTimeMillis())
        } else {
            bookDao.updateLastReadLocator(bookId, validLocator)
        }
    }

    override suspend fun getBookById(bookId: Long): BookEntity? {
        return bookDao.getBookById(bookId)
    }

    override suspend fun updateLastOpenedTimestamp(bookId: Long) {
        bookDao.updateLastOpenedTimestamp(bookId, System.currentTimeMillis())
    }

    override fun getRecentlyReadBooks(): Flow<List<LibraryBook>> {
        return bookDao.getRecentlyReadBooks().map { entities ->
            entities.map { bookEntity ->
                val progress = locatorParser.parseTotalProgression(bookEntity.lastReadLocator)
                bookEntity.toLibraryBook(progress)
            }
        }
    }

    override suspend fun updateRating(bookId: Long, rating: Int) {
        bookDao.updateRating(bookId, rating)
    }

    override fun getBookDetails(bookId: Long): Flow<BookDetails> {
        return bookDao.getBookStream(bookId).map { entity ->
            val progress = locatorParser.parseTotalProgression(entity.lastReadLocator)
            BookDetails(
                localId = entity.id,
                title = entity.title,
                author = entity.author,
                description = entity.description,
                coverImagePath = entity.coverImagePath,
                dateAdded = entity.dateAdded,
                lastReadLocator = entity.lastReadLocator,
                readingProgress = progress,
                rating = entity.rating,
                readingStatus = entity.readingStatus,
                remoteId = entity.remoteId,
                coverImageUrl = entity.coverImagePath,
                epubUrl = entity.filePath
            )
        }
    }

    override fun getOtherBooksByAuthor(author: String, currentBookId: Long): Flow<List<LibraryBook>> {
        return bookDao.getOtherBooksByAuthor(author, currentBookId).map { entities ->
            entities.map { bookEntity ->
                val progress = locatorParser.parseTotalProgression(bookEntity.lastReadLocator)
                bookEntity.toLibraryBook(progress)
            }
        }
    }

    override fun getAllReadingTimestamps(): Flow<List<Long>> {
        return bookDao.getAllTimestamps()
    }

    override fun getFinishedBookCount(): Flow<Int> {
        return bookDao.getFinishedBookCount()
    }

    override suspend fun updateReadingStatus(bookId: Long, status: ReadingStatus) {
        when (status) {
            ReadingStatus.FINISHED -> {
                bookDao.markBookAsFinished(bookId)
            }
            ReadingStatus.NOT_STARTED -> {
                resetReadingProgress(bookId)
            }
            else -> {
                bookDao.updateReadingStatus(bookId, status)
            }
        }
    }

    override fun getInProgressBooksFlow(): Flow<List<LibraryBook>> {
        return bookDao.getBooksInProgress().map { entities ->
            entities.map { bookEntity ->
                val progress = locatorParser.parseTotalProgression(bookEntity.lastReadLocator)
                bookEntity.toLibraryBook(progress)
            }
        }
    }

    override suspend fun addBook(book: Book) {
        bookDao.insertBook(book.toEntity())
    }

    override suspend fun resetReadingProgress(bookId: Long) {
        bookDao.markBookAsNotStarted(bookId)
    }

    override fun isBookInLibrary(remoteId: Int): Flow<Boolean> {
        return bookDao.isBookInLibrary(remoteId)
    }

    override suspend fun getRemoteIdForLocalId(localId: Long): String? {
        return bookDao.getRemoteIdForLocalId(localId)
    }

    override suspend fun getBookByRemoteId(remoteId: String): BookDetails? {
        return bookDao.getBookByRemoteId(remoteId)?.toBookDetails()
    }

    override fun getBookStreamByRemoteId(remoteId: String): Flow<BookDetails?> {
        return bookDao.getBookStreamByRemoteId(remoteId).map { entity ->
            entity?.toBookDetails()
        }
    }

    override suspend fun addRemoteBookToLibrary(
        filePath: String,
        remoteId: String?,
        title: String?,
        author: String?,
        description: String?
    ): Result<Long> = withContext(Dispatchers.IO) {
        val publicationResult = epubDataSource.openEpub(filePath)

        publicationResult.fold(
            onSuccess = { publication ->
                try {
                    val coverPath = saveCover(publication)

                    val bookEntity = BookEntity(
                        title = title ?: publication.metadata.title ?: "Unknown Title",
                        author = author ?: publication.metadata.authors.firstOrNull()?.name,
                        remoteId = remoteId,
                        filePath = filePath,
                        coverImagePath = coverPath,
                        description = description,
                        dateAdded = System.currentTimeMillis()
                    )

                    val newId = bookDao.insertBook(bookEntity)
                    if (newId == -1L) {
                        Result.failure(Exception("Book already exists in the library."))
                    } else {
                        Result.success(newId)
                    }
                } catch (e: Exception) {
                    Result.failure(e)
                }
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }

    override fun getTotalBookCount(): Flow<Int> {
        return bookDao.getTotalBookCount()
    }

    override suspend fun toggleArchiveStatus(bookId: Long) {
        withContext(Dispatchers.IO) {
            val currentBook = bookDao.getBookById(bookId)

            currentBook?.let { book ->
                val newArchiveStatus = !book.isArchived
                bookDao.setArchiveStatus(bookId, newArchiveStatus)
            }
        }
    }

    override suspend fun deleteBook(bookId: Long) {
        val filePath = bookDao.getBookFilePath(bookId)

        if (!filePath.isNullOrBlank()) {
            try {
                val bookFile = File(filePath)
                if (bookFile.exists()) {
                    bookFile.delete()
                }
            } catch (e: Exception) {
                Log.e("BookRepository", "Failed to delete book file: $filePath", e)
            }
        }

        bookDao.deleteBookById(bookId)
    }
}