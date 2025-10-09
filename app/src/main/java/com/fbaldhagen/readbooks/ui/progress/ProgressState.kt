package com.fbaldhagen.readbooks.ui.progress

import com.fbaldhagen.readbooks.domain.model.AchievementDetails
import com.fbaldhagen.readbooks.domain.model.ReadingAnalytics
import com.fbaldhagen.readbooks.domain.model.ReadingGoalProgress
import com.fbaldhagen.readbooks.domain.model.ReadingStreakInfo
import com.fbaldhagen.readbooks.domain.model.StreakStatus

data class ProgressState(
    val isLoading: Boolean = true,
    val readingStreakInfo: ReadingStreakInfo = ReadingStreakInfo(0, StreakStatus.INACTIVE),
    val finishedBookCount: Int = 0,
    val readingGoalProgress: ReadingGoalProgress = ReadingGoalProgress(0, 15),
    val readingAnalytics: ReadingAnalytics = ReadingAnalytics(),
    val achievements: List<AchievementDetails> = emptyList(),
    val isGoalDialogVisible: Boolean = false,
    val selectedAchievement: AchievementDetails? = null,
    val error: String? = null
)