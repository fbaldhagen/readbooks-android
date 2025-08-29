package com.fbaldhagen.readbooks.data.repository

import com.fbaldhagen.readbooks.data.datasource.local.db.ReadingSessionDao
import com.fbaldhagen.readbooks.data.model.ReadingSessionEntity
import com.fbaldhagen.readbooks.domain.model.ReadingSession
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: ReadingSessionDao
) : SessionRepository {

    override suspend fun startSession(bookId: Long) {
        val newSession = ReadingSessionEntity(
            bookId = bookId,
            startTimeMillis = System.currentTimeMillis()
        )
        sessionDao.insertSession(newSession)
    }

    override suspend fun endSession(bookId: Long, endTimeMillis: Long) {
        val activeSession = sessionDao.getActiveSessionForBook(bookId)
        activeSession?.let { session ->
            val updatedSession = session.copy(endTimeMillis = endTimeMillis)
            sessionDao.updateSession(updatedSession)
        }
    }

    override suspend fun endAllActiveSessions() {
        val activeSessions = sessionDao.getAllActiveSessions()
        if (activeSessions.isEmpty()) return

        val currentTime = System.currentTimeMillis()
        val updatedSessions = activeSessions.map {
            it.copy(endTimeMillis = currentTime)
        }

        updatedSessions.forEach { sessionDao.updateSession(it) }
    }

    override fun getAllSessions(): Flow<List<ReadingSession>> {
        return sessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTotalReadingTimeFlow(): Flow<Duration> {
        return sessionDao.getTotalReadingTimeMillisFlow().map { totalMillis ->
            (totalMillis ?: 0L).milliseconds
        }
    }

    override fun getNightOwlSessionsCountFlow(): Flow<Int> {
        return sessionDao.getNightOwlSessionCountFlow()
    }

    override suspend fun getActiveSessionForBook(bookId: Long): ReadingSession? {
        return sessionDao.getActiveSessionForBook(bookId)?.toDomain()
    }
}

private fun ReadingSessionEntity.toDomain(): ReadingSession {
    return ReadingSession(
        id = this.id,
        bookId = this.bookId,
        startTimeMillis = this.startTimeMillis,
        endTimeMillis = this.endTimeMillis
    )
}