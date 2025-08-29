package com.fbaldhagen.readbooks.ui.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.domain.model.AchievementId
import com.fbaldhagen.readbooks.domain.usecase.ResetAchievementsUseCase
import com.fbaldhagen.readbooks.domain.usecase.UpdateAchievementProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val updateAchievementProgressUseCase: UpdateAchievementProgressUseCase,
    private val resetAchievementsUseCase: ResetAchievementsUseCase
) : ViewModel() {

    fun addBookwormProgress(books: Int) {
        viewModelScope.launch {
            updateAchievementProgressUseCase(AchievementId.BOOKWORM, books)
        }
    }

    fun addPageTurnerProgress(hours: Int) {
        val minutes = hours * 60
        viewModelScope.launch {
            updateAchievementProgressUseCase(AchievementId.PAGE_TURNER, minutes)
        }
    }

    fun triggerNightOwlProgress() {
        viewModelScope.launch {
            updateAchievementProgressUseCase(AchievementId.NIGHT_OWL, 1)
        }
    }

    fun resetAchievements() {
        viewModelScope.launch {
            resetAchievementsUseCase()
        }
    }
}