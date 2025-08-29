package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.HomeContent
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetHomeContentUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    operator fun invoke(): Flow<HomeContent> {
        return bookRepository.getInProgressBooksFlow().map { inProgressBooks ->
            val currentBook = inProgressBooks.firstOrNull()

            val recentBooks = if (inProgressBooks.isNotEmpty()) {
                inProgressBooks.drop(1)
            } else {
                emptyList()
            }

            HomeContent(
                currentlyReading = currentBook,
                recentBooks = recentBooks
            )
        }
    }
}