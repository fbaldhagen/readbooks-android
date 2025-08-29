package com.fbaldhagen.readbooks.data.datasource.local.db

import androidx.room.*
import com.fbaldhagen.readbooks.data.model.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Delete
    suspend fun delete(bookmark: BookmarkEntity)

    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId ORDER BY creationDate DESC")
    fun getBookmarksForBook(bookId: Long): Flow<List<BookmarkEntity>>
}