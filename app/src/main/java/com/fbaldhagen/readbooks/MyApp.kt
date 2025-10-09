package com.fbaldhagen.readbooks

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.annotation.OptIn
import androidx.hilt.work.HiltWorkerFactory
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.work.Configuration
import com.fbaldhagen.readbooks.domain.usecase.SeedLibraryFromAssetsUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var seedLibraryFromAssetsUseCase: SeedLibraryFromAssetsUseCase

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        applicationScope.launch {
            seedLibraryFromAssetsUseCase()
        }
    }

    @OptIn(UnstableApi::class)
    private fun createNotificationChannel() {
        val channelId = DefaultMediaNotificationProvider.DEFAULT_CHANNEL_ID
        val channelName = "Audiobook Playback"
        val importance = NotificationManager.IMPORTANCE_LOW

        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Notifications for the audiobook player"
        }

        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}