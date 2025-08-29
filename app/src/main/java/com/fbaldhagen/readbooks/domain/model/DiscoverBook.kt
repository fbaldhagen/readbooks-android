package com.fbaldhagen.readbooks.domain.model

data class DiscoverBook(
    val id: Int,
    val title: String,
    val author: String?,
    val coverUrl: String?
)