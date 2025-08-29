package com.fbaldhagen.readbooks.data.datasource.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.fbaldhagen.readbooks.data.model.UserAchievementEntity
import com.fbaldhagen.readbooks.domain.model.AchievementId
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAchievementDao {

    @Upsert
    suspend fun upsert(userAchievement: UserAchievementEntity)

    @Query("SELECT * FROM user_achievements")
    fun getAllUserAchievements(): Flow<List<UserAchievementEntity>>

    @Query("SELECT * FROM user_achievements WHERE achievement_id = :id")
    suspend fun getUserAchievementById(id: AchievementId): UserAchievementEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun initializeAchievements(initialAchievements: List<UserAchievementEntity>)

    @Query("DELETE FROM user_achievements")
    suspend fun clearAll()
}