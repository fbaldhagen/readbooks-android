package com.fbaldhagen.readbooks.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.model.HomeContent
import com.fbaldhagen.readbooks.domain.usecase.GetHomeContentUseCase
import com.fbaldhagen.readbooks.domain.usecase.GetPopularBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getHomeContentUseCase: GetHomeContentUseCase,
    getPopularBooksUseCase: GetPopularBooksUseCase
) : ViewModel() {
    private val homeContentFlow: Flow<HomeContent> = getHomeContentUseCase()

    private val jumpBackInHeaders = listOf(
        "Jump Back In",
        "Pick Up Where You Left Off",
        "From Your Recent Reads"
    )

    private val discoverHeaders = listOf(
        "Discover Popular Books",
        "Trending Now",
        "What Everyone's Reading",
        "Find Your Next Obsession"
    )

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good morning!"
            in 12..17 -> "Your afternoon escape awaits."
            in 17..22 -> "Ready for an evening read?"
            else -> "Can't sleep?"
        }
    }

    val state: StateFlow<HomeState> = homeContentFlow
        .map { homeContent ->
            val isCompletelyEmpty = homeContent.currentlyReading == null && homeContent.recentBooks.isEmpty()
            HomeState(
                isLoading = false,
                greeting = if (isCompletelyEmpty) "Explore Your Next Read" else getGreeting(),
                jumpBackInHeader = jumpBackInHeaders.random(),
                discoverHeader = discoverHeaders.random(),
                currentlyReadingBook = homeContent.currentlyReading,
                recentlyReadBooks = homeContent.recentBooks,
                error = null
            )
        }
        .catch {
            emit(
                HomeState(
                    isLoading = false,
                    jumpBackInHeader = jumpBackInHeaders.first(),
                    discoverHeader = discoverHeaders.first(),
                    recentlyReadBooks = emptyList(),
                    error = "Sorry, we couldn't load your library. Please try again."
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState(isLoading = true)
        )

    val discoverBooks: Flow<PagingData<DiscoverBook>> =
        getPopularBooksUseCase().cachedIn(viewModelScope)
}