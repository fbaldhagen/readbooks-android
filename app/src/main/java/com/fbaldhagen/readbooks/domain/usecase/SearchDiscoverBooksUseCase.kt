package com.fbaldhagen.readbooks.domain.usecase

import androidx.paging.PagingData
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.repository.DiscoverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class SearchDiscoverBooksUseCase @Inject constructor(
    private val discoverRepository: DiscoverRepository
) {
    operator fun invoke(searchTerm: String): Flow<PagingData<DiscoverBook>> {
        val sanitizedTerm = searchTerm.trim()
        if (sanitizedTerm.length < 2) {
            return flowOf(PagingData.empty())
        }
        return discoverRepository.searchBooks(sanitizedTerm)
    }
}