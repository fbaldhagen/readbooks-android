package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.CollectionRepository
import javax.inject.Inject

class CreateCollectionUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository
) {
    suspend operator fun invoke(name: String) {
        if (name.isNotBlank()) {
            collectionRepository.createCollection(name.trim())
        }
    }
}