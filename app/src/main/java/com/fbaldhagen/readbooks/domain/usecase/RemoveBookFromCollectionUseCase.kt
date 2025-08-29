package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.CollectionRepository
import javax.inject.Inject

class RemoveBookFromCollectionUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository
) {
    suspend operator fun invoke(bookId: Long, collectionId: Long) {
        collectionRepository.removeBookFromCollection(bookId, collectionId)
    }
}