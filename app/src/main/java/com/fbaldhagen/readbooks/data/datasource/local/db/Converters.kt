package com.fbaldhagen.readbooks.data.datasource.local.db

import androidx.room.TypeConverter
import com.fbaldhagen.readbooks.domain.model.AchievementId
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromReadingStatus(status: ReadingStatus): String = status.name

    @TypeConverter
    fun toReadingStatus(statusString: String): ReadingStatus = ReadingStatus.valueOf(statusString)

    @TypeConverter
    fun toAchievementId(value: String) = enumValueOf<AchievementId>(value)

    @TypeConverter
    fun fromAchievementId(value: AchievementId) = value.name

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}