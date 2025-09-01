package com.fbaldhagen.readbooks.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.fbaldhagen.readbooks.data.datasource.local.db.AppDatabase
import com.fbaldhagen.readbooks.data.datasource.local.db.BookDao
import com.fbaldhagen.readbooks.data.datasource.local.db.BookmarkDao
import com.fbaldhagen.readbooks.data.datasource.local.db.CollectionDao
import com.fbaldhagen.readbooks.data.datasource.local.db.ReadingSessionDao
import com.fbaldhagen.readbooks.data.datasource.local.db.UserAchievementDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "readbooks-db"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideBookDao(database: AppDatabase): BookDao {
        return database.bookDao()
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideBookmarkDao(database: AppDatabase): BookmarkDao {
        return database.bookmarkDao()
    }

    @Provides
    @Singleton
    fun provideReadingSessionDao(database: AppDatabase): ReadingSessionDao {
        return database.readingSessionDao()
    }

    @Provides
    @Singleton
    fun provideUserAchievementDao(database: AppDatabase): UserAchievementDao {
        return database.userAchievementDao()
    }

    @Provides
    @Singleton
    fun provideCollectionDao(database: AppDatabase): CollectionDao {
        return database.collectionDao()
    }
}