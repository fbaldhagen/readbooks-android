package com.fbaldhagen.readbooks.ui.library

import com.fbaldhagen.readbooks.domain.model.LibraryBook
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.model.SortType
import com.fbaldhagen.readbooks.ui.common.TopBarState

data class LibraryState(
    val isLoading: Boolean = true,
    val books: List<LibraryBook> = emptyList(),
    val displayMode: DisplayMode = DisplayMode.GRID,
    val activeSort: SortType = SortType.DATE_ADDED_DESC,
    val activeFilters: Set<ReadingStatus> = emptySet(),
    val totalBookCountInLibrary: Int = 0,
    val topBarState: TopBarState = TopBarState.Standard(),
    val isSortSheetVisible: Boolean = false,
    val isFilterSheetVisible: Boolean = false,
    val error: String? = null,
    val activePrimaryFilter: LibraryFilter = LibraryFilter.All,
    val isArchiveVisible: Boolean = false
)

enum class DisplayMode { GRID, LIST }

sealed interface LibraryFilter {
    data object All : LibraryFilter
    data class ByStatus(val status: ReadingStatus) : LibraryFilter
}

enum class FilterSource {
    CHIPS, ADVANCED
}