package com.fbaldhagen.readbooks.data.datasource.remote

import com.fbaldhagen.readbooks.data.model.remote.BookDetailDto
import com.fbaldhagen.readbooks.data.model.remote.GutendexResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface GutendexApiService {

    @GET("books")
    suspend fun getBooks(
        @Query("search") searchTerm: String? = null,
        @Query("topic") topic: String? = null,
        @Query("page") page: Int? = null
    ): GutendexResponseDto

    @GET
    suspend fun getBooksByUrl(@Url url: String): GutendexResponseDto

    @GET("books/{id}")
    suspend fun getBookDetails(@Path("id") bookId: String): BookDetailDto
}