package com.fbaldhagen.readbooks.data.datasource.local.db

import androidx.room.*
import com.fbaldhagen.readbooks.data.model.BookCollectionCrossRefEntity
import com.fbaldhagen.readbooks.data.model.CollectionEntity
import com.fbaldhagen.readbooks.data.model.CollectionWithBooks
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBookToCollection(crossRef: BookCollectionCrossRefEntity)

    @Delete
    suspend fun removeBookFromCollection(crossRef: BookCollectionCrossRefEntity)

    @Transaction
    suspend fun deleteCollection(collectionId: Long) {
        deleteCollectionById(collectionId)
        deleteCrossRefsByCollectionId(collectionId)
    }

    @Transaction
    @Query("SELECT * FROM collections WHERE id = :collectionId")
    fun getCollectionWithBooks(collectionId: Long): Flow<CollectionWithBooks>

    @Query("DELETE FROM collections WHERE id = :collectionId")
    suspend fun deleteCollectionById(collectionId: Long)

    @Query("DELETE FROM book_collection_cross_ref WHERE collectionId = :collectionId")
    suspend fun deleteCrossRefsByCollectionId(collectionId: Long)

    @Query("SELECT * FROM collections ORDER BY createdAt DESC")
    fun getCollections(): Flow<List<CollectionEntity>>

    @Transaction
    @Query("SELECT * FROM collections ORDER BY createdAt DESC")
    fun getCollectionsWithBooks(): Flow<List<CollectionWithBooks>>

    @Query("UPDATE collections SET name = :newName WHERE id = :collectionId")
    suspend fun renameCollection(collectionId: Long, newName: String)
}