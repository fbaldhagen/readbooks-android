package com.fbaldhagen.readbooks.domain.repository

import androidx.paging.PagingData
import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import kotlinx.coroutines.flow.Flow

interface DiscoverRepository {
    fun getDiscoverBooks(searchTerm: String?): Flow<PagingData<DiscoverBook>>

    fun getDiscoverBooksByTopic(topic: String): Flow<PagingData<DiscoverBook>>

    suspend fun getRemoteBookDetails(remoteId: String): BookDetails
}