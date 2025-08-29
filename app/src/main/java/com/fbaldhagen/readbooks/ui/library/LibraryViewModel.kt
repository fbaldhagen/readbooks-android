package com.fbaldhagen.readbooks.ui.library

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.domain.model.FilterState
import com.fbaldhagen.readbooks.domain.model.LibraryBook
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.model.SortType
import com.fbaldhagen.readbooks.domain.usecase.GetLibraryBooksUseCase
import com.fbaldhagen.readbooks.domain.usecase.GetTotalBookCountUseCase
import com.fbaldhagen.readbooks.ui.common.TopBarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getLibraryBooksUseCase: GetLibraryBooksUseCase,
    private val getTotalBookCountUseCase: GetTotalBookCountUseCase
) : ViewModel() {

    // Internal mutable state for user selections
    private val _activeSort = MutableStateFlow(SortType.DATE_ADDED_DESC)
    private val _activeFilters = MutableStateFlow(FilterState())
    private val _displayMode = MutableStateFlow(DisplayMode.GRID)
    private val _isSortSheetVisible = MutableStateFlow(false)
    private val _isFilterSheetVisible = MutableStateFlow(false)

    private val _isSearching = MutableStateFlow(false)
    private val _searchQuery = MutableStateFlow(TextFieldValue(""))

    private val _activeFilterSource = MutableStateFlow(FilterSource.CHIPS)
    private val _primaryFilter = MutableStateFlow<LibraryFilter>(LibraryFilter.All)

    private data class LibraryData(
        val books: List<LibraryBook>,
        val totalCount: Int
    )

    private data class BaseUiConfig(
        val displayMode: DisplayMode,
        val activeSort: SortType,
        val activeFilters: FilterState,
        val isSortSheetVisible: Boolean,
        val isFilterSheetVisible: Boolean
    )

    private data class UiConfig(
        val displayMode: DisplayMode,
        val activeSort: SortType,
        val activeFilters: FilterState,
        val isSortSheetVisible: Boolean,
        val isFilterSheetVisible: Boolean,
        val topBarState: TopBarState
    )

    private val effectiveFilterStateFlow: Flow<FilterState> = combine(
        _primaryFilter,
        _activeFilters,
        _activeFilterSource
    ) { primary, advanced, source ->
        when (source) {
            FilterSource.CHIPS -> when (primary) {
                is LibraryFilter.All -> FilterState()
                is LibraryFilter.ByStatus -> FilterState(statuses = setOf(primary.status))
            }
            FilterSource.ADVANCED -> advanced
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val libraryDataFlow = combine(
        _activeSort,
        effectiveFilterStateFlow,
        _searchQuery.debounce(300L)
    ) { sort, effectiveFilter, query ->
        GetLibraryBooksUseCase.Params(query = query.text, sortType = sort, filters = effectiveFilter)
    }.flatMapLatest { params ->
        combine(
            getLibraryBooksUseCase(params).catch { emit(emptyList()) },
            getTotalBookCountUseCase().catch { emit(0) }
        ) { books, totalCount ->
            LibraryData(books, totalCount)
        }
    }

    private val topBarStateFlow: Flow<TopBarState> = combine(
        _isSearching,
        _searchQuery
    ) { isSearching, query ->
        if (isSearching) {
            TopBarState.Search(
                query = query,
                onQueryChange = { newQuery -> onSearchQueryChanged(newQuery) },
                onClose = ::onSearchClosed,
                hint = "Search your library..."
            )
        } else {
            TopBarState.Standard()
        }
    }

    private val uiConfigFlow = combine(
        _displayMode,
        _activeSort,
        _activeFilters,
        _isSortSheetVisible,
        _isFilterSheetVisible
    ) { displayMode, sort, filters, isSortVisible, isFilterVisible ->
        BaseUiConfig(displayMode, sort, filters, isSortVisible, isFilterVisible)
    }.combine(topBarStateFlow) { baseConfig, topBarState ->
        UiConfig(
            displayMode = baseConfig.displayMode,
            activeSort = baseConfig.activeSort,
            activeFilters = baseConfig.activeFilters,
            isSortSheetVisible = baseConfig.isSortSheetVisible,
            isFilterSheetVisible = baseConfig.isFilterSheetVisible,
            topBarState = topBarState
        )
    }

    val state: StateFlow<LibraryState> = combine(
        libraryDataFlow,
        uiConfigFlow,
        _primaryFilter
    ) { data, config, primaryFilter  ->
        LibraryState(
            isLoading = false,
            books = data.books,
            totalBookCountInLibrary = data.totalCount,
            displayMode = config.displayMode,
            activeSort = config.activeSort,
            activeFilters = config.activeFilters.statuses,
            isSortSheetVisible = config.isSortSheetVisible,
            isFilterSheetVisible = config.isFilterSheetVisible,
            topBarState = config.topBarState,
            activePrimaryFilter = primaryFilter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryState(isLoading = true)
    )

    // --- Event Handlers for UI Actions ---

    fun onSortChanged(sortType: SortType) {
        _activeSort.value = sortType
        _isSortSheetVisible.value = false
    }

    fun onFilterToggled(status: ReadingStatus) {
        _activeFilterSource.value = FilterSource.ADVANCED

        _activeFilters.update { currentFilters ->
            val newStatuses = currentFilters.statuses.toMutableSet()
            if (newStatuses.contains(status)) {
                newStatuses.remove(status)
            } else {
                newStatuses.add(status)
            }
            currentFilters.copy(statuses = newStatuses)
        }
    }

    fun onClearFilters() {
        _activeFilterSource.value = FilterSource.ADVANCED
        _activeFilters.value = FilterState()
        _isFilterSheetVisible.value = false
    }

    fun onToggleDisplayMode() {
        _displayMode.update { if (it == DisplayMode.GRID) DisplayMode.LIST else DisplayMode.GRID }
    }

    // --- UI Visibility Handlers ---

    fun onSortClicked() {
        _isSortSheetVisible.value = true
    }

    fun onFilterClicked() {
        _isFilterSheetVisible.value = true
    }

    fun onDismissSortSheet() {
        _isSortSheetVisible.value = false
    }

    fun onDismissFilterSheet() {
        _isFilterSheetVisible.value = false
    }

    fun onSearchOpened() {
        _isSearching.value = true
    }

    fun onSearchClosed() {
        _isSearching.value = false
        _searchQuery.value = TextFieldValue("")
    }

    fun onSearchQueryChanged(query: TextFieldValue) {
        _searchQuery.value = query
    }

    fun onPrimaryFilterChanged(filter: LibraryFilter) {
        _activeFilterSource.value = FilterSource.CHIPS
        _primaryFilter.value = filter
    }
}