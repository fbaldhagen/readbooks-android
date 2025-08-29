package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.ReadingStreakInfo
import com.fbaldhagen.readbooks.domain.model.StreakStatus
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class GetReadingStreakUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    operator fun invoke(): Flow<ReadingStreakInfo> {
        return bookRepository.getAllReadingTimestamps().map { timestamps ->
            if (timestamps.isEmpty()) {
                return@map ReadingStreakInfo(0, StreakStatus.INACTIVE)
            }

            val uniqueDays = timestamps
                .map { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
                .toSet()
                .sortedDescending()

            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            val mostRecentDay = uniqueDays.first()

            if (mostRecentDay != today && mostRecentDay != yesterday) {
                return@map ReadingStreakInfo(0, StreakStatus.INACTIVE)
            }

            var streakCount = 1
            for (i in 1 until uniqueDays.size) {
                if (uniqueDays[i] == uniqueDays[i - 1].minusDays(1)) {
                    streakCount++
                } else {
                    break
                }
            }

            val status = when {
                mostRecentDay == today -> StreakStatus.COMPLETED_TODAY
                streakCount > 0 -> StreakStatus.IN_PROGRESS
                else -> StreakStatus.INACTIVE
            }

            ReadingStreakInfo(count = streakCount, status = status)
        }
    }
}