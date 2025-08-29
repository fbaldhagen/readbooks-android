package com.fbaldhagen.readbooks.domain.model

import org.readium.r2.shared.publication.Locator

data class Bookmark(
    val id: Long,
    val bookId: Long,
    val creationDate: Long,
    val locator: Locator,
    val textSnippet: String?
)