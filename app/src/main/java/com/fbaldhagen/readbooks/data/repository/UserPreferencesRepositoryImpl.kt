package com.fbaldhagen.readbooks.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.fbaldhagen.readbooks.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {
    private val prefs = context.getSharedPreferences("user_goals", Context.MODE_PRIVATE)
    private val goalKey = "yearly_book_goal"

    override fun getReadingGoal(): Flow<Int> = callbackFlow {

        val defaultGoal = 15

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == goalKey) {
                trySend(prefs.getInt(goalKey, defaultGoal))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)

        trySend(prefs.getInt(goalKey, defaultGoal))

        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun setReadingGoal(goal: Int) {
        prefs.edit {
            putInt(goalKey, goal)
        }
    }
}