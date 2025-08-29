package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow
import org.readium.r2.shared.publication.Locator

interface BookmarkRepository {

    fun getBookmarksForBook(bookId: Long): Flow<List<Bookmark>>
    suspend fun addBookmark(bookId: Long, locator: Locator)
    suspend fun deleteBookmark(bookmark: Bookmark)

}