package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.BookRepository
import javax.inject.Inject

class DeleteBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: Long) {
        bookRepository.deleteBook(bookId)
    }
}