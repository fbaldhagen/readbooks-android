package com.fbaldhagen.readbooks.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.fbaldhagen.readbooks.data.datasource.local.db.DiscoverBookDao
import com.fbaldhagen.readbooks.data.datasource.remote.DiscoverPagingSource
import com.fbaldhagen.readbooks.data.datasource.remote.GutendexApiService
import com.fbaldhagen.readbooks.data.mapper.toBookDetails
import com.fbaldhagen.readbooks.data.model.remote.DiscoverQuery
import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.repository.DiscoverRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscoverRepositoryImpl @Inject constructor(
    private val gutendexApiService: GutendexApiService,
    private val discoverBookDao: DiscoverBookDao
) : DiscoverRepository {

    override fun getPopularBooks(): Flow<PagingData<DiscoverBook>> {
        return Pager(
            config = PagingConfig(pageSize = 32),
            pagingSourceFactory = {
                DiscoverPagingSource(
                    query = DiscoverQuery.Popular,
                    apiService = gutendexApiService,
                    bookDao = discoverBookDao
                )
            }
        ).flow
    }

    override fun getBooksByTopic(topic: String): Flow<PagingData<DiscoverBook>> {
        return Pager(
            config = PagingConfig(pageSize = 32),
            pagingSourceFactory = {
                DiscoverPagingSource(
                    query = DiscoverQuery.ByTopic(topic),
                    apiService = gutendexApiService,
                    bookDao = discoverBookDao
                )
            }
        ).flow
    }

    override fun searchBooks(searchTerm: String): Flow<PagingData<DiscoverBook>> {
        return Pager(
            config = PagingConfig(pageSize = 32),
            pagingSourceFactory = {
                DiscoverPagingSource(
                    query = DiscoverQuery.BySearch(searchTerm),
                    apiService = gutendexApiService,
                    bookDao = discoverBookDao
                )
            }
        ).flow
    }

    override suspend fun getRemoteBookDetails(remoteId: String): BookDetails {
        val bookDetailDto = gutendexApiService.getBookDetails(remoteId)
        return bookDetailDto.toBookDetails()
    }
}