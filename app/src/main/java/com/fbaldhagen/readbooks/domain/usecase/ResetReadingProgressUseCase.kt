package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.BookRepository
import javax.inject.Inject

class ResetReadingProgressUseCase @Inject constructor(
    private val bookRepository: BookRepository
){
    suspend operator fun invoke(bookId: Long) {
        bookRepository.resetReadingProgress(bookId)
    }
}