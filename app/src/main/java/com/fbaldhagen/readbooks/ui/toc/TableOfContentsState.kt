package com.fbaldhagen.readbooks.ui.toc

import com.fbaldhagen.readbooks.domain.model.TocItem

data class TableOfContentsState(
    val isLoading: Boolean = true,
    val tocItems: List<TocItem> = emptyList(),
    val bookTitle: String = "",
    val error: String? = null
)