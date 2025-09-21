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

    suspend fun fromBookId(bookId: Long): Result<Publication> {
        val filePath = bookRepository.getBookFilePath(bookId)
            ?: return Result.failure(Exception("No book found with ID $bookId"))

        return invoke(filePath)
    }

    suspend fun fromBookIdWithCover(bookId: Long): Result<PublicationWithCover> {
        val bookEntity = bookRepository.getBookById(bookId)
            ?: return Result.failure(Exception("No book found with ID $bookId"))

        val publicationResult = invoke(bookEntity.filePath)

        return publicationResult.map { publication ->
            PublicationWithCover(
                publication = publication,
                coverImagePath = bookEntity.coverImagePath
            )
        }
    }
}

data class PublicationWithCover(
    val publication: Publication,
    val coverImagePath: String?
)