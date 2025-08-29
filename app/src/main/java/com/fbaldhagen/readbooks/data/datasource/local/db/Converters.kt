package com.fbaldhagen.readbooks.data.datasource.local.db

import androidx.room.TypeConverter
import com.fbaldhagen.readbooks.domain.model.AchievementId
import com.fbaldhagen.readbooks.domain.model.ReadingStatus

class Converters {
    @TypeConverter
    fun fromReadingStatus(status: ReadingStatus): String = status.name

    @TypeConverter
    fun toReadingStatus(statusString: String): ReadingStatus = ReadingStatus.valueOf(statusString)

    @TypeConverter
    fun toAchievementId(value: String) = enumValueOf<AchievementId>(value)

    @TypeConverter
    fun fromAchievementId(value: AchievementId) = value.name
}