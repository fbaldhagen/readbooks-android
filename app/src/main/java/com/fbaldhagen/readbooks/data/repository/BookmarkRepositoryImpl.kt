package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.data.datasource.local.db.BookmarkDao
import com.fbaldhagen.readbooks.data.model.BookmarkEntity
import com.fbaldhagen.readbooks.domain.model.Bookmark
import com.fbaldhagen.readbooks.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import org.readium.r2.shared.publication.Locator
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {

    override fun getBookmarksForBook(bookId: Long): Flow<List<Bookmark>> {
        return bookmarkDao.getBookmarksForBook(bookId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addBookmark(bookId: Long, locator: Locator) {
        val entity = BookmarkEntity(
            bookId = bookId,
            locator = locator.toJSON().toString(),
            progression = locator.locations.progression,
            textSnippet = locator.text.highlight
        )
        bookmarkDao.insert(entity)
    }

    override suspend fun deleteBookmark(bookmark: Bookmark) {
        val entity = BookmarkEntity(
            id = bookmark.id,
            bookId = bookmark.bookId,
            creationDate = bookmark.creationDate,
            locator = bookmark.locator.toJSON().toString(),
            progression = bookmark.locator.locations.progression,
            textSnippet = bookmark.locator.text.highlight
        )
        bookmarkDao.delete(entity)
    }
}

private fun BookmarkEntity.toDomain(): Bookmark {
    val locator = Locator.fromJSON(JSONObject(this.locator))
        ?: throw IllegalStateException("Failed to parse locator from database")

    return Bookmark(
        id = this.id,
        bookId = this.bookId,
        creationDate = this.creationDate,
        locator = locator,
        textSnippet = this.textSnippet
    )
}