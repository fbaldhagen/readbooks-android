package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.AchievementRepository
import javax.inject.Inject

class ResetAchievementsUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository
) {
    suspend operator fun invoke() {
        achievementRepository.resetAllAchievements()
    }
}