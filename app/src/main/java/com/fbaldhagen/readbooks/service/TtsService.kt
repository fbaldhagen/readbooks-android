package com.fbaldhagen.readbooks.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import org.readium.navigator.media.tts.AndroidTtsNavigator
import org.readium.r2.shared.ExperimentalReadiumApi
import javax.inject.Inject

@OptIn(ExperimentalReadiumApi::class)
@UnstableApi
@AndroidEntryPoint
class TtsService : MediaSessionService() {

    @Inject
    lateinit var bookRepository: BookRepository

    class Session(
        val bookId: Long,
        val navigator: AndroidTtsNavigator,
        val mediaSession: MediaSession,
    ) {
        val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        val isPlaying: StateFlow<Boolean> = callbackFlow {
            val listener = object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    trySend(isPlaying)
                }
            }
            mediaSession.player.addListener(listener)
            trySend(mediaSession.player.isPlaying)

            awaitClose {
                mediaSession.player.removeListener(listener)
            }
        }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), false)
    }

    inner class Binder : android.os.Binder() {

        private val sessionMutable: MutableStateFlow<Session?> =
            MutableStateFlow(null)

        val session: StateFlow<Session?> =
            sessionMutable.asStateFlow()

        fun closeSession() {
            session.value?.let { session ->
                session.mediaSession.release()
                session.coroutineScope.cancel()
                session.navigator.close()
                sessionMutable.value = null
            }
        }


        @OptIn(FlowPreview::class)
        fun openSession(navigator: AndroidTtsNavigator, bookId: Long) {
            val activityIntent = createSessionActivityIntent()

            val mediaSession = MediaSession.Builder(applicationContext, navigator.asMedia3Player())
                .setSessionActivity(activityIntent)
                .setId(bookId.toString())
                .build()

            addSession(mediaSession)

            val session = Session(
                bookId,
                navigator,
                mediaSession
            )

            sessionMutable.value = session

            navigator.currentLocator
                .sample(3000)
                .onEach { locator ->
                    bookRepository.saveReadingProgress(bookId, locator.toJSON().toString())
                }.launchIn(session.coroutineScope)
        }

        @SuppressLint("ObsoleteSdkInt")
        private fun createSessionActivityIntent(): PendingIntent {
            var flags = PendingIntent.FLAG_UPDATE_CURRENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags = flags or PendingIntent.FLAG_IMMUTABLE
            }

            val intent = application.packageManager.getLaunchIntentForPackage(
                application.packageName
            )
            return PendingIntent.getActivity(applicationContext, 0, intent, flags)
        }

        fun stop() {
            closeSession()
            ServiceCompat.stopForeground(this@TtsService, ServiceCompat.STOP_FOREGROUND_REMOVE)
            this@TtsService.stopSelf()
        }
    }

    private val binder by lazy {
        Binder()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return if (intent?.action == "com.fbaldhagen.readbooks.service.TTS_SERVICE_INTERFACE") {
            super.onBind(intent)
            binder
        } else {
            super.onBind(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (binder.session.value == null) {
            val notification =
                NotificationCompat.Builder(
                    this,
                    DefaultMediaNotificationProvider.DEFAULT_CHANNEL_ID
                )
                    .setContentTitle("Audiobook Service")
                    .setContentText("Service is shutting down.")
                    .build()

            startForeground(DefaultMediaNotificationProvider.DEFAULT_NOTIFICATION_ID, notification)
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            stopSelf(startId)
        }

        // Prevents the service from being automatically restarted after being killed;
        return START_NOT_STICKY
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return binder.session.value?.mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // Close the session to allow the service to be stopped.
        binder.closeSession()
        binder.stop()
    }

    override fun onDestroy() {
        binder.closeSession()
        // Ensure one more time that all notifications are gone and,
        // hopefully, pending intents cancelled.
        NotificationManagerCompat.from(this).cancelAll()
        super.onDestroy()
    }
}