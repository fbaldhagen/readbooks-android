package com.fbaldhagen.readbooks.ui.reader

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fbaldhagen.readbooks.R
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import com.fbaldhagen.readbooks.domain.usecase.GetPublicationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.readium.navigator.media.common.MediaNavigator
import org.readium.navigator.media.tts.AndroidTtsNavigatorFactory
import org.readium.navigator.media.tts.TtsNavigator
import org.readium.navigator.media.tts.android.AndroidTtsEngine
import org.readium.navigator.media.tts.android.AndroidTtsPreferences
import org.readium.navigator.media.tts.android.AndroidTtsSettings
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import javax.inject.Inject

private const val TAG = "TtsService"
private const val NOTIFICATION_ID = 1001
private const val NOTIFICATION_CHANNEL_ID = "readbooks_tts_channel"

@OptIn(ExperimentalReadiumApi::class)
private typealias AndroidTtsNavigator = TtsNavigator<
        AndroidTtsSettings,
        AndroidTtsPreferences,
        AndroidTtsEngine.Error,
        AndroidTtsEngine.Voice
        >

@AndroidEntryPoint
class TtsService : Service() {

    @Inject
    lateinit var getPublicationUseCase: GetPublicationUseCase

    @Inject
    lateinit var bookRepository: BookRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @OptIn(ExperimentalReadiumApi::class)
    private var ttsNavigator: AndroidTtsNavigator? = null
    private var publication: Publication? = null
    private var bookId: Long = -1

    private var pendingLocator: Locator? = null

    data class TtsServiceState(
        val isReady: Boolean = false,
        val playbackState: TtsPlaybackState = TtsPlaybackState.IDLE,
        val currentLocator: Locator? = null,
        val errorMessage: String? = null
    )

    private val _state = MutableStateFlow(TtsServiceState())
    val state = _state.asStateFlow()

    inner class TtsBinder : Binder() {
        fun getService(): TtsService = this@TtsService
    }

    private val binder = TtsBinder()

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val receivedBookId = intent.getLongExtra(EXTRA_BOOK_ID, -1)
                val locatorJson = intent.getStringExtra(EXTRA_LOCATOR_JSON)
                val initialLocator = locatorJson?.let { Locator.fromJSON(JSONObject(it)) }

                if (receivedBookId != -1L) {
                    this.bookId = receivedBookId
                    serviceScope.launch {
                        initializeTts(receivedBookId, initialLocator)
                    }
                } else {
                    Log.e(TAG, "Tried to start service without a valid bookId.")
                    stopSelf()
                }
            }
            ACTION_PLAY_PAUSE -> if (state.value.isReady) {
                if (state.value.playbackState == TtsPlaybackState.PLAYING) pause() else play()
            }
            ACTION_STOP -> stop()
        }
        return START_NOT_STICKY
    }

    @OptIn(ExperimentalReadiumApi::class)
    private suspend fun initializeTts(bookId: Long, initialLocator: Locator?) {
        Log.e(TAG, "Attempting to initialize TTS with locator: ${initialLocator.toString()}")

        this.pendingLocator = initialLocator

        val book = bookRepository.getBookById(bookId)
        if (book == null) {
            _state.update { it.copy(errorMessage = "Book not found.") }
            stopSelf()
            return
        }

        getPublicationUseCase(book.filePath).fold(
            onSuccess = { pub ->
                this.publication = pub
                val factory = AndroidTtsNavigatorFactory(application, pub)
                if (factory == null) {
                    _state.update { it.copy(errorMessage = "This book cannot be read aloud.") }
                    return
                }

                factory.createNavigator(
                    initialLocator = initialLocator,
                    listener = object : TtsNavigator.Listener {
                        override fun onStopRequested() = stop()
                    }
                )
                    .onSuccess { navigator ->
                        ttsNavigator = navigator

                        observeNavigatorState()
                        _state.update { it.copy(isReady = true) }
                    }
                    .onFailure { error ->
                        Log.e(TAG, "Failed to create TtsNavigator: $error")
                        _state.update { it.copy(errorMessage = "Failed to start text-to-speech.") }
                        stopSelf()
                    }
            },
            onFailure = { error ->
                _state.update { it.copy(errorMessage = "Failed to load book for TTS: ${error.message}") }
                stopSelf()
            }
        )
    }

    @OptIn(ExperimentalReadiumApi::class)
    private fun observeNavigatorState() {
        val navigator = ttsNavigator ?: return
        serviceScope.launch {
            navigator.playback.collectLatest { playback ->
                if (pendingLocator != null && playback.state is MediaNavigator.State.Ready) {
                    val locatorToGo = pendingLocator!!
                    pendingLocator = null
                    Log.d(TAG, "Navigator is READY. Executing go() to locator: $locatorToGo")
                    navigator.go(locatorToGo)
                }

                val newPlaybackState = when {
                    playback.playWhenReady -> TtsPlaybackState.PLAYING
                    playback.state is MediaNavigator.State.Ended -> TtsPlaybackState.FINISHED
                    playback.state is TtsNavigator.State.Failure -> TtsPlaybackState.ERROR
                    else -> TtsPlaybackState.PAUSED
                }

                val error = (playback.state as? TtsNavigator.State.Failure)?.error?.message
                _state.update { it.copy(playbackState = newPlaybackState, errorMessage = error) }

                if (newPlaybackState == TtsPlaybackState.PLAYING) {
                    startForeground(NOTIFICATION_ID, buildNotification())
                } else {
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    if (newPlaybackState == TtsPlaybackState.PAUSED) {
                        notificationManager.notify(NOTIFICATION_ID, buildNotification())
                    } else {
                        stop()
                    }
                }
            }
        }

        serviceScope.launch {
            navigator.location
                .map { it.utteranceLocator }
                .distinctUntilChanged()
                .collectLatest { locator ->
                    _state.update { it.copy(currentLocator = locator) }
                }
        }
    }

    @OptIn(ExperimentalReadiumApi::class)
    fun play() { ttsNavigator?.play() }

    @OptIn(ExperimentalReadiumApi::class)
    fun pause() { ttsNavigator?.pause() }

    @OptIn(ExperimentalReadiumApi::class)
    fun stop() {
        ttsNavigator?.close()
        ttsNavigator = null
        publication = null
        bookId = -1
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun buildNotification(): Notification {
        createNotificationChannel()

        val readerIntent = if (bookId != -1L) {
            ReaderActivity.createIntent(this, bookId)
        } else {
            Intent(this, ReaderActivity::class.java)
        }.apply { flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP }
        val contentPendingIntent = PendingIntent.getActivity(this, 0, readerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val playPauseAction = if (state.value.playbackState == TtsPlaybackState.PLAYING) {
            NotificationCompat.Action(R.drawable.ic_pause, "Pause", createActionIntent(ACTION_PLAY_PAUSE))
        } else {
            NotificationCompat.Action(R.drawable.ic_play, "Play", createActionIntent(ACTION_PLAY_PAUSE))
        }

        val stopAction = NotificationCompat.Action(R.drawable.ic_stop, "Stop", createActionIntent(ACTION_STOP))

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(publication?.metadata?.title ?: "Reading Aloud")
            .setContentText("Tap to return to the reader")
            .setSmallIcon(R.drawable.ic_read_aloud)
            .setContentIntent(contentPendingIntent)
            .setOngoing(state.value.playbackState == TtsPlaybackState.PLAYING)
            .addAction(playPauseAction)
            .addAction(stopAction)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1))
            .build()
    }

    private fun createActionIntent(action: String): PendingIntent {
        val intent = Intent(this, TtsService::class.java).apply { this.action = action }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Text-to-Speech", NotificationManager.IMPORTANCE_LOW)
            .apply { description = "Controls for the read-aloud feature" }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    @OptIn(ExperimentalReadiumApi::class)
    override fun onDestroy() {
        ttsNavigator?.close()
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.fbaldhagen.readbooks.ui.reader.TTS_ACTION_START"
        const val ACTION_PLAY_PAUSE = "com.fbaldhagen.readbooks.ui.reader.TTS_ACTION_PLAY_PAUSE"
        const val ACTION_STOP = "com.fbaldhagen.readbooks.ui.reader.TTS_ACTION_STOP"
        const val EXTRA_BOOK_ID = "com.fbaldhagen.readbooks.ui.reader.EXTRA_BOOK_ID"
        const val EXTRA_LOCATOR_JSON = "com.fbaldhagen.readbooks.ui.reader.EXTRA_LOCATOR_JSON"
    }
}