package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.LibraryBook
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOtherBooksByAuthorUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {

    operator fun invoke(author: String, excludeBookId: Long): Flow<List<LibraryBook>> {
        return bookRepository.getOtherBooksByAuthor(author, excludeBookId)
    }
}