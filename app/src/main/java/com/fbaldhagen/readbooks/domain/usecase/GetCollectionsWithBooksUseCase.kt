package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.CollectionWithBooks
import com.fbaldhagen.readbooks.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCollectionsWithBooksUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository
) {
    operator fun invoke(): Flow<List<CollectionWithBooks>> {
        return collectionRepository.getCollectionsWithBooks()
    }
}