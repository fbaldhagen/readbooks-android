package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.CollectionRepository
import javax.inject.Inject

class RenameCollectionUseCase @Inject constructor(
    private val collectionRepository: CollectionRepository
) {
    suspend operator fun invoke(collectionId: Long, newName: String) {
        if (newName.isNotBlank()) {
            collectionRepository.renameCollection(collectionId, newName.trim())
        }
    }
}