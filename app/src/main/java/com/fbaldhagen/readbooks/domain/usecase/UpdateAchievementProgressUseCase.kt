package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.AchievementId
import com.fbaldhagen.readbooks.domain.repository.AchievementRepository
import javax.inject.Inject

class UpdateAchievementProgressUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository
) {

    suspend operator fun invoke(id: AchievementId, progressToAdd: Int) {
        if (progressToAdd <= 0) return

        val definition = achievementRepository.getAchievementDefinition(id)
        val currentProgress = achievementRepository.getUserAchievement(id)

        if (definition == null || currentProgress == null) {
            return
        }

        val maxTier = definition.tiers.size
        if (currentProgress.unlockedTier >= maxTier) {
            val updatedProgress = currentProgress.copy(
                currentProgress = currentProgress.currentProgress + progressToAdd
            )
            achievementRepository.updateUserAchievement(updatedProgress)
            return
        }

        val newProgressValue = currentProgress.currentProgress + progressToAdd
        var newUnlockedTier = currentProgress.unlockedTier

        while (true) {
            val nextTierToUnlock = definition.tiers.getOrNull(newUnlockedTier) ?: break
            if (newProgressValue >= nextTierToUnlock.threshold) {
                newUnlockedTier++
            } else {
                break
            }
        }

        var newUnlockedDate = currentProgress.unlockedDate

        if (newUnlockedTier > currentProgress.unlockedTier && currentProgress.unlockedDate == null) {
            newUnlockedDate = java.time.LocalDate.now()
        }

        val finalProgress = currentProgress.copy(
            currentProgress = newProgressValue,
            unlockedTier = newUnlockedTier,
            unlockedDate = newUnlockedDate
        )
        achievementRepository.updateUserAchievement(finalProgress)
    }
}