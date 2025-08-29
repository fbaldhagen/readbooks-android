package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.domain.model.CollectionWithBooks
import com.fbaldhagen.readbooks.domain.model.Collection
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {

    suspend fun createCollection(name: String)

    suspend fun deleteCollection(collectionId: Long)

    suspend fun renameCollection(collectionId: Long, newName: String)

    suspend fun addBookToCollection(bookId: Long, collectionId: Long)

    suspend fun removeBookFromCollection(bookId: Long, collectionId: Long)

    fun getCollections(): Flow<List<Collection>>

    fun getCollectionsWithBooks(): Flow<List<CollectionWithBooks>>

    fun getCollectionWithBooks(collectionId: Long): Flow<CollectionWithBooks>
}