package com.fbaldhagen.readbooks.di

import com.fbaldhagen.readbooks.data.datasource.local.file.EpubDataSource
import com.fbaldhagen.readbooks.data.datasource.local.file.FileDataSource
import com.fbaldhagen.readbooks.data.datasource.local.file.FileDataSourceImpl
import com.fbaldhagen.readbooks.data.datasource.local.file.ReadiumEpubDataSource
import com.fbaldhagen.readbooks.data.parser.ReadiumMetadataParser
import com.fbaldhagen.readbooks.data.repository.AchievementRepositoryImpl
import com.fbaldhagen.readbooks.data.repository.BookRepositoryImpl
import com.fbaldhagen.readbooks.data.repository.BookmarkRepositoryImpl
import com.fbaldhagen.readbooks.data.repository.CollectionRepositoryImpl
import com.fbaldhagen.readbooks.data.repository.DiscoverRepositoryImpl
import com.fbaldhagen.readbooks.data.repository.SessionRepositoryImpl
import com.fbaldhagen.readbooks.data.repository.UserPreferencesRepositoryImpl
import com.fbaldhagen.readbooks.domain.parser.EpubMetadataParser
import com.fbaldhagen.readbooks.domain.repository.AchievementRepository
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import com.fbaldhagen.readbooks.domain.repository.BookmarkRepository
import com.fbaldhagen.readbooks.domain.repository.CollectionRepository
import com.fbaldhagen.readbooks.domain.repository.DiscoverRepository
import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import com.fbaldhagen.readbooks.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBookRepository(
        bookRepositoryImpl: BookRepositoryImpl
    ): BookRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(
        bookmarkRepositoryImpl: BookmarkRepositoryImpl
    ): BookmarkRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        sessionRepositoryImpl: SessionRepositoryImpl
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindAchievementRepository(
        achievementRepositoryImpl: AchievementRepositoryImpl
    ): AchievementRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindCollectionRepository(
        collectionRepositoryImpl: CollectionRepositoryImpl
    ): CollectionRepository

    @Binds
    @Singleton
    abstract fun bindDiscoverRepository(
        impl: DiscoverRepositoryImpl
    ): DiscoverRepository

    @Binds
    @Singleton
    abstract fun bindEpubDataSource(
        readiumEpubDataSource: ReadiumEpubDataSource
    ): EpubDataSource

    @Binds
    @Singleton
    abstract fun bindEpubMetadataParser(
        impl: ReadiumMetadataParser
    ): EpubMetadataParser

    @Binds
    @Singleton
    abstract fun bindFileDataSource(
        fileDataSourceImpl: FileDataSourceImpl
    ): FileDataSource
}