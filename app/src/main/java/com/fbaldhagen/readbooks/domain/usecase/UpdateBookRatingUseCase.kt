package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.BookRepository
import javax.inject.Inject

class UpdateBookRatingUseCase  @Inject constructor(
    private val bookRepository: BookRepository
){
    suspend operator fun invoke(bookId: Long, rating: Int) {
        if (rating in 0..5) {
            bookRepository.updateRating(bookId, rating)
        }
    }
}