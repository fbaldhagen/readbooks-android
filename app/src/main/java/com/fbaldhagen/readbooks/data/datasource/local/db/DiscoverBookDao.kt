package com.fbaldhagen.readbooks.data.datasource.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fbaldhagen.readbooks.data.model.DiscoverBookEntity

@Dao
interface DiscoverBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<DiscoverBookEntity>)

    @Query("DELETE FROM discover_books WHERE `query` = :query")
    suspend fun clearByQuery(query: String)

    @Query("SELECT addedAt FROM discover_books WHERE `query` = :query ORDER BY addedAt DESC LIMIT 1")
    suspend fun getLastUpdateTime(query: String): Long?

    @Query("SELECT * FROM discover_books WHERE `query` = :query")
    suspend fun getCachedBooks(query: String): List<DiscoverBookEntity>
}