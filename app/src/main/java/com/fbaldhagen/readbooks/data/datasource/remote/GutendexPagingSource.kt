package com.fbaldhagen.readbooks.data.datasource.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fbaldhagen.readbooks.data.mapper.toDomain
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import retrofit2.HttpException
import java.io.IOException

class GutendexPagingSource(
    private val gutendexApiService: GutendexApiService,
    private val searchTerm: String? = null,
    private val topic: String? = null
) : PagingSource<String, DiscoverBook>() {

    override fun getRefreshKey(state: PagingState<String, DiscoverBook>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, DiscoverBook> {
        return try {
            val response = if (params.key == null) {
                gutendexApiService.getBooks(searchTerm = searchTerm, topic = topic)
            } else {
                gutendexApiService.getBooksByUrl(url = params.key!!)
            }

            val discoverBooks = response.results.map { it.toDomain() }

            LoadResult.Page(
                data = discoverBooks,
                prevKey = response.previous,
                nextKey = response.next
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}