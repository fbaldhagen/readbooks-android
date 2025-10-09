package com.fbaldhagen.readbooks.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.domain.model.AchievementDetails
import com.fbaldhagen.readbooks.domain.model.ReadingAnalytics
import com.fbaldhagen.readbooks.domain.model.ReadingGoalProgress
import com.fbaldhagen.readbooks.domain.model.ReadingStreakInfo
import com.fbaldhagen.readbooks.domain.usecase.GetAchievementsUseCase
import com.fbaldhagen.readbooks.domain.usecase.GetFinishedBookCountUseCase
import com.fbaldhagen.readbooks.domain.usecase.GetReadingAnalyticsUseCase
import com.fbaldhagen.readbooks.domain.usecase.GetReadingGoalProgressUseCase
import com.fbaldhagen.readbooks.domain.usecase.GetReadingStreakUseCase
import com.fbaldhagen.readbooks.domain.usecase.SetReadingGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    getReadingStreakUseCase: GetReadingStreakUseCase,
    getFinishedBookCountUseCase: GetFinishedBookCountUseCase,
    getReadingGoalProgressUseCase: GetReadingGoalProgressUseCase,
    getReadingAnalyticsUseCase: GetReadingAnalyticsUseCase,
    getAchievementsUseCase: GetAchievementsUseCase,
    private val setReadingGoalUseCase: SetReadingGoalUseCase
) : ViewModel() {

    private val _isGoalDialogVisible = MutableStateFlow(false)
    private val _selectedAchievement = MutableStateFlow<AchievementDetails?>(null)

    private data class ProfileStatsData(
        val streak: ReadingStreakInfo,
        val finishedCount: Int,
        val goalProgress: ReadingGoalProgress,
        val analytics: ReadingAnalytics
    )

    private val profileStatsFlow = combine(
        getReadingStreakUseCase(),
        getFinishedBookCountUseCase(),
        getReadingGoalProgressUseCase(),
        getReadingAnalyticsUseCase()
    ) { streak, finishedCount, goalProgress, analytics ->
        ProfileStatsData(streak, finishedCount, goalProgress, analytics)
    }

    val state: StateFlow<ProgressState> = combine(
        profileStatsFlow,
        getAchievementsUseCase(),
        _isGoalDialogVisible,
        _selectedAchievement
    ) { stats, achievements, isDialogVisible, selectedAchievement ->
        ProgressState(
            isLoading = false,
            readingStreakInfo = stats.streak,
            finishedBookCount = stats.finishedCount,
            readingGoalProgress = stats.goalProgress,
            readingAnalytics = stats.analytics,
            achievements = achievements,
            isGoalDialogVisible = isDialogVisible,
            selectedAchievement = selectedAchievement
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProgressState()
    )

    fun onGoalClicked() { _isGoalDialogVisible.value = true }
    fun onDismissGoalDialog() { _isGoalDialogVisible.value = false }
    fun onSaveGoal(newGoal: Int) {
        viewModelScope.launch {
            setReadingGoalUseCase(newGoal)
            _isGoalDialogVisible.value = false
        }
    }

    fun onAchievementClicked(achievement: AchievementDetails) { _selectedAchievement.value = achievement }
    fun onDismissAchievementDetails() { _selectedAchievement.value = null }
}