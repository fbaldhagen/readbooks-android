package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.BookRepository
import javax.inject.Inject

class AddBookToLibraryUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(filePath: String): Result<Long> {
        return bookRepository.addBookToLibrary(filePath)
    }
}