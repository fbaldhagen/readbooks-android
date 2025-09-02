package com.fbaldhagen.readbooks.domain.repository

import androidx.paging.PagingData
import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import kotlinx.coroutines.flow.Flow

interface DiscoverRepository {
    fun getPopularBooks(): Flow<PagingData<DiscoverBook>>
    fun getBooksByTopic(topic: String): Flow<PagingData<DiscoverBook>>
    fun searchBooks(searchTerm: String): Flow<PagingData<DiscoverBook>>

    suspend fun getRemoteBookDetails(remoteId: String): BookDetails
}