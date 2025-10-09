package com.fbaldhagen.readbooks.domain.model

import androidx.annotation.DrawableRes

data class AchievementDetails(
    val definition: Achievement,
    val userProgress: UserAchievement
)

enum class AchievementId {
    BOOKWORM,       // Finish X books
    NIGHT_OWL,      // Read after midnight
    STREAK_STAR,    // Achieve a reading streak of X days
    PAGE_TURNER     // Read for X hours total
}

data class AchievementTier(
    val name: String,
    val threshold: Int,
    val rewardPoints: Int = 0
)

data class Achievement(
    val id: AchievementId,
    val name: String,
    val description: String,
    @DrawableRes val iconRes: Int,
    val tiers: List<AchievementTier>
)

data class UserAchievement(
    val achievementId: AchievementId,
    val currentProgress: Int,
    val unlockedTier: Int,
    val unlockedDate: java.time.LocalDate? = null,
    val lastProgressTimestamp: Long? = null
)