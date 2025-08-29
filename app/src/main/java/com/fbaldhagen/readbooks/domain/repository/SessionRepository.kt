package com.fbaldhagen.readbooks.domain.repository

import com.fbaldhagen.readbooks.domain.model.ReadingSession
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface SessionRepository {

    suspend fun startSession(bookId: Long)

    suspend fun endSession(bookId: Long, endTimeMillis: Long)

    suspend fun endAllActiveSessions()

    fun getAllSessions(): Flow<List<ReadingSession>>

    fun getTotalReadingTimeFlow(): Flow<Duration>

    fun getNightOwlSessionsCountFlow(): Flow<Int>

    suspend fun getActiveSessionForBook(bookId: Long): ReadingSession?
}