package com.fbaldhagen.readbooks.data.datasource.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fbaldhagen.readbooks.data.model.ReadingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ReadingSessionEntity): Long

    @Update
    suspend fun updateSession(session: ReadingSessionEntity)

    @Query("SELECT * FROM reading_sessions WHERE book_id = :bookId AND end_time_millis IS NULL LIMIT 1")
    suspend fun getActiveSessionForBook(bookId: Long): ReadingSessionEntity?

    @Query("SELECT * FROM reading_sessions ORDER BY start_time_millis DESC")
    fun getAllSessions(): Flow<List<ReadingSessionEntity>>

    @Query("SELECT * FROM reading_sessions WHERE end_time_millis IS NULL")
    suspend fun getAllActiveSessions(): List<ReadingSessionEntity>

    @Query("""
        SELECT SUM(end_time_millis - start_time_millis) 
        FROM reading_sessions 
        WHERE end_time_millis IS NOT NULL
    """)
    fun getTotalReadingTimeMillisFlow(): Flow<Long?>

    @Query("""
        SELECT COUNT(*) 
        FROM reading_sessions 
        WHERE end_time_millis IS NOT NULL 
        AND CAST(strftime('%H', start_time_millis / 1000, 'unixepoch') AS INTEGER) >= 0 
        AND CAST(strftime('%H', start_time_millis / 1000, 'unixepoch') AS INTEGER) < 5
    """)
    fun getNightOwlSessionCountFlow(): Flow<Int>
}