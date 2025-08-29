package com.fbaldhagen.readbooks.domain.model

import org.readium.r2.shared.publication.Href

data class TocItem(
    val title: String,
    val href: Href,
    val children: List<TocItem> = emptyList(),
    val level: Int = 0
)