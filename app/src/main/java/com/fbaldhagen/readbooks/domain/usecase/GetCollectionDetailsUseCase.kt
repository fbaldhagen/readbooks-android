package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.CollectionWithBooks
import com.fbaldhagen.readbooks.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCollectionDetailsUseCase @Inject constructor(
    private val repository: CollectionRepository
) {
    operator fun invoke(collectionId: Long): Flow<CollectionWithBooks> {
        return repository.getCollectionWithBooks(collectionId)
    }
}