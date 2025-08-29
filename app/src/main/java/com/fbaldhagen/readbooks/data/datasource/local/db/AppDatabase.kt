package com.fbaldhagen.readbooks.data.datasource.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fbaldhagen.readbooks.data.model.BookCollectionCrossRefEntity
import com.fbaldhagen.readbooks.data.model.CollectionEntity
import com.fbaldhagen.readbooks.data.model.BookEntity
import com.fbaldhagen.readbooks.data.model.BookmarkEntity
import com.fbaldhagen.readbooks.data.model.ReadingSessionEntity
import com.fbaldhagen.readbooks.data.model.UserAchievementEntity

@Database(
    entities = [
        BookEntity::class,
        BookmarkEntity::class,
        ReadingSessionEntity::class,
        UserAchievementEntity::class,
        CollectionEntity::class,
        BookCollectionCrossRefEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun readingSessionDao(): ReadingSessionDao
    abstract fun userAchievementDao(): UserAchievementDao
    abstract fun collectionDao(): CollectionDao

    companion object {
    }
}