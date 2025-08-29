package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.BookRepository
import org.readium.r2.shared.publication.Publication
import javax.inject.Inject

class GetPublicationUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(filePath: String): Result<Publication> {
        return bookRepository.openBook(filePath)
    }
}