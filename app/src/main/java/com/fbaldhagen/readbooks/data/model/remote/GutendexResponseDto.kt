package com.fbaldhagen.readbooks.data.model.remote

import kotlinx.serialization.Serializable

@Serializable
data class GutendexResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<BookDto>
)