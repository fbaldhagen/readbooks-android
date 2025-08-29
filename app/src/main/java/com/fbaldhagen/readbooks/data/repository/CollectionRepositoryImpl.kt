package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.data.datasource.local.db.CollectionDao
import com.fbaldhagen.readbooks.data.model.BookCollectionCrossRefEntity
import com.fbaldhagen.readbooks.data.model.CollectionEntity
import com.fbaldhagen.readbooks.data.mapper.toLibraryBook
import com.fbaldhagen.readbooks.data.parser.LocatorParser
import com.fbaldhagen.readbooks.domain.repository.CollectionRepository
import com.fbaldhagen.readbooks.domain.model.Collection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.fbaldhagen.readbooks.data.model.CollectionWithBooks as DataCollectionWithBooks
import com.fbaldhagen.readbooks.domain.model.CollectionWithBooks as DomainCollectionWithBooks

class CollectionRepositoryImpl @Inject constructor(
    private val collectionDao: CollectionDao,
    private val locatorParser: LocatorParser
) : CollectionRepository {

    override suspend fun createCollection(name: String) {
        val collectionEntity = CollectionEntity(name = name)
        collectionDao.insertCollection(collectionEntity)
    }

    override suspend fun deleteCollection(collectionId: Long) {
        collectionDao.deleteCollection(collectionId)
    }

    override suspend fun renameCollection(collectionId: Long, newName: String) {
        collectionDao.renameCollection(collectionId, newName)
    }

    override suspend fun addBookToCollection(bookId: Long, collectionId: Long) {
        val crossRef = BookCollectionCrossRefEntity(bookId = bookId, collectionId = collectionId)
        collectionDao.addBookToCollection(crossRef)
    }

    override suspend fun removeBookFromCollection(bookId: Long, collectionId: Long) {
        val crossRef = BookCollectionCrossRefEntity(bookId = bookId, collectionId = collectionId)
        collectionDao.removeBookFromCollection(crossRef)
    }

    override fun getCollections(): Flow<List<Collection>> {
        return collectionDao.getCollections().map { entityList ->
            entityList.map { it.toDomain() }
        }
    }

    override fun getCollectionsWithBooks(): Flow<List<DomainCollectionWithBooks>> {
        return collectionDao.getCollectionsWithBooks().map { dataList ->
            dataList.map {
                it.toDomain()
            }
        }
    }

    override fun getCollectionWithBooks(collectionId: Long): Flow<DomainCollectionWithBooks> {
        return collectionDao.getCollectionWithBooks(collectionId).map { entity ->
            entity.toDomain()
        }
    }

    private fun CollectionEntity.toDomain(): Collection {
        return Collection(
            id = this.id,
            name = this.name,
            createdAt = this.createdAt
        )
    }

    private fun DataCollectionWithBooks.toDomain(): DomainCollectionWithBooks {
        return DomainCollectionWithBooks(
            collection = this.collection.toDomain(),
            books = this.books.map { bookEntity ->
                val progress = locatorParser.parseTotalProgression(bookEntity.lastReadLocator)
                bookEntity.toLibraryBook(progress)
            }
        )
    }
}