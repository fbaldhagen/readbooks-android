package com.fbaldhagen.readbooks.ui.discover

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.paging.PagingData
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import kotlinx.coroutines.flow.Flow

@Immutable
data class DiscoverState(
    val searchQuery: TextFieldValue = TextFieldValue(""),
    val content: DiscoverContent = DiscoverContent.Loading
)

@Immutable
data class DiscoverSection(
    val title: String,
    val topic: String,
    val books: Flow<PagingData<DiscoverBook>>
)


@Immutable
sealed interface DiscoverContent {
    data object Loading : DiscoverContent

    data class Browsing(val sections: List<DiscoverSectionInfo>) : DiscoverContent

    data class Searching(val searchResults: Flow<PagingData<DiscoverBook>>) : DiscoverContent
}

data class DiscoverSectionInfo(
    val title: String,
    val topic: String
)
