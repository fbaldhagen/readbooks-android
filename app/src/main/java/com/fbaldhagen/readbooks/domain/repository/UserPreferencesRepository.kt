package com.fbaldhagen.readbooks.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    fun getReadingGoal(): Flow<Int>
    suspend fun setReadingGoal(goal: Int)
}