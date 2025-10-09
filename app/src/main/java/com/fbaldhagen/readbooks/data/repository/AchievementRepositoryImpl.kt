package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.R
import com.fbaldhagen.readbooks.data.datasource.local.db.UserAchievementDao
import com.fbaldhagen.readbooks.data.model.UserAchievementEntity
import com.fbaldhagen.readbooks.domain.model.Achievement
import com.fbaldhagen.readbooks.domain.model.AchievementDetails
import com.fbaldhagen.readbooks.domain.model.AchievementId
import com.fbaldhagen.readbooks.domain.model.AchievementTier
import com.fbaldhagen.readbooks.domain.model.UserAchievement
import com.fbaldhagen.readbooks.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepositoryImpl @Inject constructor(
    private val userAchievementDao: UserAchievementDao
) : AchievementRepository {

    private val isInitialized = AtomicBoolean(false)

    override suspend fun initializeAchievements() {
        if (isInitialized.getAndSet(true)) return

        val initialEntities = allAchievementsMap.values.map {
            UserAchievementEntity(achievementId = it.id)
        }
        userAchievementDao.initializeAchievements(initialEntities)
    }

    private val allAchievementsMap: Map<AchievementId, Achievement> = listOf(
        Achievement(
            id = AchievementId.BOOKWORM,
            name = "Bookworm",
            description = "Finish %d books",
            iconRes = R.drawable.book_worm,
            tiers = listOf(
                AchievementTier("Bronze", 1),
                AchievementTier("Silver", 10),
                AchievementTier("Gold", 50)
            )
        ),
        Achievement(
            id = AchievementId.PAGE_TURNER,
            name = "Page Turner",
            description = "Read for a total of %d minutes",
            iconRes = R.drawable.page_turner,
            tiers = listOf(
                AchievementTier("Bronze", threshold = 10 * 60),
                AchievementTier("Silver", threshold = 50 * 60),
                AchievementTier("Gold", threshold = 200 * 60)
            )
        ),
        Achievement(
            id = AchievementId.NIGHT_OWL,
            name = "Night Owl",
            description = "Read after midnight %d times",
            iconRes = R.drawable.night_owl,
            tiers = listOf(
                AchievementTier("Bronze", 1),
                AchievementTier("Silver", 5),
                AchievementTier("Gold", 20)
            )
        ),
        Achievement(
            id = AchievementId.STREAK_STAR,
            name = "Streak Star",
            description = "Maintain a reading streak for %d days",
            iconRes = R.drawable.streak_star_v2,
            tiers = listOf(
                AchievementTier("Bronze", 7),
                AchievementTier("Silver", 30),
                AchievementTier("Gold", 100)
            )
        )
    ).associateBy { it.id }

    override fun getAchievementDetails(): Flow<List<AchievementDetails>> {
        return flow {
            initializeAchievements()
            val detailsFlow = userAchievementDao.getAllUserAchievements().map { progressList ->
                val progressMap = progressList.associateBy { it.achievementId }
                allAchievementsMap.values.map { definition ->
                    val userProgress = progressMap[definition.id]?.toDomain()
                        ?: UserAchievement(definition.id, 0, 0)
                    AchievementDetails(definition, userProgress)
                }
            }
            emitAll(detailsFlow)
        }
    }

    override suspend fun getAchievementDefinition(id: AchievementId): Achievement? {
        return allAchievementsMap[id]
    }

    override suspend fun getUserAchievement(id: AchievementId): UserAchievement {
        return userAchievementDao.getUserAchievementById(id)?.toDomain()
            ?: UserAchievement(achievementId = id, currentProgress = 0, unlockedTier = 0)
    }

    override suspend fun updateUserAchievement(userAchievement: UserAchievement) {
        userAchievementDao.upsert(userAchievement.toEntity())
    }

    override suspend fun resetAllAchievements() {
        userAchievementDao.clearAll()
        initializeAchievements()
    }
}

private fun UserAchievementEntity.toDomain(): UserAchievement {
    return UserAchievement(
        achievementId = this.achievementId,
        currentProgress = this.currentProgress,
        unlockedTier = this.unlockedTier,
        unlockedDate = this.unlockedDate,
        lastProgressTimestamp = this.lastProgressTimestamp
    )
}

private fun UserAchievement.toEntity(): UserAchievementEntity {
    return UserAchievementEntity(
        achievementId = this.achievementId,
        currentProgress = this.currentProgress,
        unlockedTier = this.unlockedTier,
        unlockedDate = this.unlockedDate,
        lastProgressTimestamp = this.lastProgressTimestamp
    )
}