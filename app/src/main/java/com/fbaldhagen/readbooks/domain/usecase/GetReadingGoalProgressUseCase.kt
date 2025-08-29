package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.ReadingGoalProgress
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import com.fbaldhagen.readbooks.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetReadingGoalProgressUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<ReadingGoalProgress> {
        val finishedCountFlow = bookRepository.getFinishedBookCount()
        val goalFlow = userPreferencesRepository.getReadingGoal()

        return combine(finishedCountFlow, goalFlow) { count, goal ->
            ReadingGoalProgress(currentCount = count, goal = goal)
        }
    }
}