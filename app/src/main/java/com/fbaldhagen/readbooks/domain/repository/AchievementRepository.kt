package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.domain.model.Achievement
import com.fbaldhagen.readbooks.domain.model.AchievementDetails
import com.fbaldhagen.readbooks.domain.model.AchievementId
import com.fbaldhagen.readbooks.domain.model.UserAchievement
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {

    fun getAchievementDetails(): Flow<List<AchievementDetails>>
    suspend fun getAchievementDefinition(id: AchievementId): Achievement?
    suspend fun getUserAchievement(id: AchievementId): UserAchievement?
    suspend fun updateUserAchievement(userAchievement: UserAchievement)
    suspend fun initializeAchievements()
    suspend fun resetAllAchievements()
}