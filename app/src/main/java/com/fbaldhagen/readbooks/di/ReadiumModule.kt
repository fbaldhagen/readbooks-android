package com.fbaldhagen.readbooks.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import org.readium.r2.streamer.parser.PublicationParser
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReadiumModule {

    @Provides
    @Singleton
    fun provideHttpClient(): DefaultHttpClient = DefaultHttpClient()

    @Provides
    @Singleton
    fun provideAssetRetriever(
        @ApplicationContext context: Context,
        httpClient: DefaultHttpClient
    ): AssetRetriever = AssetRetriever(context.contentResolver, httpClient)

    @Provides
    @Singleton
    fun providePublicationParser(
        @ApplicationContext context: Context,
        httpClient: DefaultHttpClient,
        assetRetriever: AssetRetriever,
    ): PublicationParser = DefaultPublicationParser(context, httpClient, assetRetriever, pdfFactory = null)

    @Provides
    @Singleton
    fun providePublicationOpener(
        publicationParser: PublicationParser
    ): PublicationOpener = PublicationOpener(publicationParser)
}