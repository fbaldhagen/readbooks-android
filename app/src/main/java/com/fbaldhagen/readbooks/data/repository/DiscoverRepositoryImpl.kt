package com.fbaldhagen.readbooks.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.fbaldhagen.readbooks.data.datasource.remote.GutendexApiService
import com.fbaldhagen.readbooks.data.datasource.remote.GutendexPagingSource
import com.fbaldhagen.readbooks.data.mapper.toBookDetails
import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.repository.DiscoverRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscoverRepositoryImpl @Inject constructor(
    private val gutendexApiService: GutendexApiService
) : DiscoverRepository {

    override fun getDiscoverBooks(searchTerm: String?): Flow<PagingData<DiscoverBook>> {
        return Pager(
            config = PagingConfig(
                pageSize = 32,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GutendexPagingSource(gutendexApiService, searchTerm = searchTerm)
            }
        ).flow
    }

    override fun getDiscoverBooksByTopic(topic: String): Flow<PagingData<DiscoverBook>> {
        return Pager(
            config = PagingConfig(pageSize = 32, enablePlaceholders = false),
            pagingSourceFactory = {
                GutendexPagingSource(
                    gutendexApiService = gutendexApiService,
                    topic = topic
                )
            }
        ).flow
    }


    override suspend fun getRemoteBookDetails(remoteId: String): BookDetails {
        val bookDetailDto = gutendexApiService.getBookDetails(remoteId)
        return bookDetailDto.toBookDetails()
    }
}