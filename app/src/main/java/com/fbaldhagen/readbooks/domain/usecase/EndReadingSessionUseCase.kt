package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.AchievementId
import com.fbaldhagen.readbooks.domain.repository.AchievementRepository
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EndReadingSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val achievementRepository: AchievementRepository,
    private val updateAchievementProgressUseCase: UpdateAchievementProgressUseCase
) {

    suspend operator fun invoke(bookId: Long) {
        val activeSession = sessionRepository.getActiveSessionForBook(bookId) ?: return
        val endTimeMillis = System.currentTimeMillis()
        val sessionStartTime = activeSession.startTimeMillis

        val durationMillis = endTimeMillis - sessionStartTime
        val durationInMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis).toInt()
        if (durationInMinutes > 0) {
            updateAchievementProgressUseCase(
                AchievementId.PAGE_TURNER,
                durationInMinutes
            )
        }

        if (isNightOwlSession(sessionStartTime)) {
            awardNightOwlPointIfNeeded(sessionStartTime)
        }

        sessionRepository.endSession(bookId, endTimeMillis)
    }

    private suspend fun awardNightOwlPointIfNeeded(sessionStartTime: Long) {
        val nightOwlProgress = achievementRepository.getUserAchievement(AchievementId.NIGHT_OWL) ?: return

        val sessionDate = Instant.ofEpochMilli(sessionStartTime)
            .atZone(ZoneId.systemDefault()).toLocalDate()

        val lastAwardedDate = nightOwlProgress.lastProgressTimestamp?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
        }

        if (lastAwardedDate == null || sessionDate.isAfter(lastAwardedDate)) {
            val updatedProgress = nightOwlProgress.copy(
                currentProgress = nightOwlProgress.currentProgress + 1,
                lastProgressTimestamp = sessionStartTime
            )
            achievementRepository.updateUserAchievement(updatedProgress)
        }
    }

    private fun isNightOwlSession(startTimeMillis: Long): Boolean {
        val hour = Instant.ofEpochMilli(startTimeMillis)
            .atZone(ZoneId.systemDefault()).hour
        return hour in 0..4
    }
}
