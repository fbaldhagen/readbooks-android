package com.fbaldhagen.readbooks.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fbaldhagen.readbooks.domain.model.AchievementId

@Entity(tableName = "user_achievements")
data class UserAchievementEntity(
    @PrimaryKey
    @ColumnInfo(name = "achievement_id")
    val achievementId: AchievementId,

    @ColumnInfo(name = "current_progress")
    val currentProgress: Int = 0,

    @ColumnInfo(name = "unlocked_tier")
    val unlockedTier: Int = 0,

    @ColumnInfo(name = "last_progress_timestamp")
    val lastProgressTimestamp: Long? = null
)