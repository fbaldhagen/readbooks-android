package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.FilterState
import com.fbaldhagen.readbooks.domain.model.LibraryBook
import com.fbaldhagen.readbooks.domain.model.SortType
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLibraryBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    operator fun invoke(params: Params): Flow<List<LibraryBook>> {
        return bookRepository.getLibraryBooks(
            query = params.query,
            sortType = params.sortType,
            filters = params.filters
        )
    }

    data class Params(
        val query: String,
        val sortType: SortType,
        val filters: FilterState
    )
}