package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.ReadingAnalytics
import com.fbaldhagen.readbooks.domain.model.ReadingSession
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


class GetReadingAnalyticsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {

    operator fun invoke(): Flow<ReadingAnalytics> {
        return sessionRepository.getAllSessions().map { sessions ->
            val completedSessions = sessions.filter { it.endTimeMillis != null }
            val totalMillis = completedSessions.sumOf { session ->
                session.endTimeMillis!! - session.startTimeMillis
            }
            val longestStreak = calculateLongestStreak(sessions)

            val averageSessionDuration = if (completedSessions.isNotEmpty()) {
                val totalDurationOfCompletedSessions = completedSessions
                    .sumOf { (it.endTimeMillis!! - it.startTimeMillis) }
                    .milliseconds

                totalDurationOfCompletedSessions / completedSessions.size
            } else {
                Duration.ZERO
            }

            ReadingAnalytics(
                totalReadingTime = totalMillis.milliseconds,
                longestStreakInDays = longestStreak,
                averageSessionDuration = averageSessionDuration
            )
        }
    }

    private fun calculateLongestStreak(sessions: List<ReadingSession>): Int {
        if (sessions.isEmpty()) return 0
        val readingDays = sessions
            .map { Instant.ofEpochMilli(it.startTimeMillis).atZone(ZoneId.systemDefault()).toLocalDate() }
            .toSet()
            .sorted()

        if (readingDays.size <= 1) return readingDays.size

        var longestStreak = 1
        var currentStreak = 1

        for (i in 1 until readingDays.size) {
            val currentDay = readingDays[i]
            val previousDay = readingDays[i - 1]

            if (currentDay.isEqual(previousDay.plusDays(1))) {
                currentStreak++
            } else {
                longestStreak = maxOf(longestStreak, currentStreak)
                currentStreak = 1
            }
        }

        return maxOf(longestStreak, currentStreak)
    }
}