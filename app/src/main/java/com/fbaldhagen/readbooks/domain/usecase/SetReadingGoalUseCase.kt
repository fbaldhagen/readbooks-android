package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SetReadingGoalUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(goal: Int) {
        if (goal > 0) {
            userPreferencesRepository.setReadingGoal(goal)
        }
    }
}