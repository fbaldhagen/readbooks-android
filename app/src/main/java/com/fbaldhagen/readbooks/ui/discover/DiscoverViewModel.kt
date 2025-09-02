package com.fbaldhagen.readbooks.ui.discover

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.usecase.GetDiscoverBooksByTopicUseCase
import com.fbaldhagen.readbooks.domain.usecase.SearchDiscoverBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val searchDiscoverBooks: SearchDiscoverBooksUseCase,
    private val getDiscoverBooksByTopic: GetDiscoverBooksByTopicUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow(TextFieldValue(""))

    @OptIn(FlowPreview::class)
    private val searchResults: Flow<PagingData<DiscoverBook>> = _searchQuery
        .debounce(500L)
        .flatMapLatest { query ->
            searchDiscoverBooks(query.text)
        }
        .cachedIn(viewModelScope)

    private val browseTopics = mapOf(
        "Classic Adventures" to "adventure",
        "Science Fiction" to "science fiction",
        "Tales of Mystery" to "mystery",
        "Fantasy Worlds" to "fantasy",
        "Children's novels" to "children"
    )

    private val browseSections: List<DiscoverSectionInfo> = browseTopics.map { (title, topic) ->
        DiscoverSectionInfo(title = title, topic = topic)
    }

    private val bookTopicFlows = mutableMapOf<String, Flow<PagingData<DiscoverBook>>>()
    fun getBooksForTopic(topic: String): Flow<PagingData<DiscoverBook>> {
        return bookTopicFlows.getOrPut(topic) {
            getDiscoverBooksByTopic(topic).cachedIn(viewModelScope)
        }
    }

    private val contentFlow: Flow<DiscoverContent> = _searchQuery.map { query ->
        if (query.text.isBlank()) {
            DiscoverContent.Browsing(browseSections)
        } else {
            DiscoverContent.Searching(searchResults)
        }
    }

    val state: StateFlow<DiscoverState> = combine(
        _searchQuery,
        contentFlow
    ) { query, content ->
        DiscoverState(
            searchQuery = query,
            content = content
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DiscoverState(searchQuery = TextFieldValue(""))
    )

    fun onSearchQueryChanged(query: TextFieldValue) {
        _searchQuery.value = query
    }

    fun onSearchClosed() {
        _searchQuery.value = TextFieldValue("")
    }
}