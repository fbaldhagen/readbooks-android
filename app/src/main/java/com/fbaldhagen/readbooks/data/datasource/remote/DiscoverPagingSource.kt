package com.fbaldhagen.readbooks.data.datasource.remote

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fbaldhagen.readbooks.data.datasource.local.db.DiscoverBookDao
import com.fbaldhagen.readbooks.data.mapper.toDomain
import com.fbaldhagen.readbooks.data.mapper.toEntity
import com.fbaldhagen.readbooks.domain.model.DiscoverBook


class DiscoverPagingSource(
    private val query: String,
    private val apiService: GutendexApiService,
    private val bookDao: DiscoverBookDao
) : PagingSource<Int, DiscoverBook>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DiscoverBook> {
        val page = params.key ?: 1

        return try {
            if (page == 1) {
                val lastUpdateTime = bookDao.getLastUpdateTime(query)
                val isCacheStale = lastUpdateTime == null ||
                        (System.currentTimeMillis() - lastUpdateTime) > CACHE_TIMEOUT_MILLIS

                if (isCacheStale) {
                    val response = apiService.getBooks(page = 1, searchTerm = query.takeIf { it != "popular" })
                    val bookEntities = response.results.map { it.toEntity(query) }

                    bookDao.clearByQuery(query)
                    bookDao.insertAll(bookEntities)

                    val nextPage = if (response.next == null) null else 2
                    LoadResult.Page(
                        data = bookEntities.map { it.toDomain() },
                        prevKey = null,
                        nextKey = nextPage
                    )
                } else {
                    val cachedBooks = bookDao.getCachedBooks(query)
                    LoadResult.Page(
                        data = cachedBooks.map { it.toDomain() },
                        prevKey = null,
                        nextKey = 2
                    )
                }
            }
            else {
                val response = apiService.getBooks(page = page, searchTerm = query.takeIf { it != "popular" })
                val books = response.results.map { it.toDomain() }
                val nextPage = if (response.next == null) {
                    null
                } else {
                    Uri.parse(response.next).getQueryParameter("page")?.toInt()
                }

                LoadResult.Page(
                    data = books,
                    prevKey = page - 1,
                    nextKey = nextPage
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DiscoverBook>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val CACHE_TIMEOUT_MILLIS = 86_400_000L // 24 hrs
    }
}