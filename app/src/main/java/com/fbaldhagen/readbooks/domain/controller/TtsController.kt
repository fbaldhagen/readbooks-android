package com.fbaldhagen.readbooks.domain.controller

import android.app.Application
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.fbaldhagen.readbooks.domain.manager.TtsManager
import com.fbaldhagen.readbooks.domain.usecase.GetPublicationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.readium.navigator.media.tts.AndroidTtsNavigator
import org.readium.navigator.media.tts.AndroidTtsNavigatorFactory
import org.readium.navigator.media.tts.TtsNavigator
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalReadiumApi::class, ExperimentalCoroutinesApi::class)
@UnstableApi
@Singleton
class TtsController @Inject constructor(
    private val ttsManager: TtsManager,
    private val getPublicationUseCase: GetPublicationUseCase,
    private val application: Application
) : TtsNavigator.Listener {
    init {
        Log.d("TTS_DEBUG", "TtsController instance created: ${this.hashCode()}")
    }

    private val coroutineScope: CoroutineScope = MainScope()

    private val activeNavigator: AndroidTtsNavigator?
        get() = ttsManager.session.value?.navigator

    val isPlaying: StateFlow<Boolean> =
        ttsManager.session.flatMapLatest { session ->
            session?.isPlaying ?: flowOf(false)
        }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), false)

    val currentBookId: StateFlow<Long?> =
        ttsManager.session.map { it?.bookId }
            .stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), null)

    val currentLocator: StateFlow<Locator?> =
        ttsManager.session.flatMapLatest { session ->
            session?.navigator?.currentLocator ?: flowOf(null)
        }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), null)

    fun play(bookId: Long, initialLocator: Locator? = null) {
        coroutineScope.launch {
            val activeSession = ttsManager.session.value
            if (activeSession?.bookId == bookId) {
                activeSession.mediaSession.player.play()
                return@launch
            }

            launchNewSession(bookId, initialLocator)
        }
    }

    fun togglePlayPause() {
        val player = ttsManager.session.value?.mediaSession?.player ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun stop() {
        ttsManager.closeSession()
    }

    fun next() {
        activeNavigator?.skipToNextUtterance()
    }

    fun previous() {
        activeNavigator?.skipToPreviousUtterance()
    }

    override fun onStopRequested() {
        stop()
    }

    private suspend fun launchNewSession(bookId: Long, initialLocator: Locator?) {
        ttsManager.closeSession()
        Log.d("TTS", "launchNewSession(): initial progression=${initialLocator}")

        getPublicationUseCase.fromBookId(bookId).fold(
            onSuccess = { publication ->
                val navigatorFactory = AndroidTtsNavigatorFactory(
                    application,
                    publication
                )

                val navigatorResult = navigatorFactory?.createNavigator(
                    listener = this,
                    initialLocator = initialLocator
                )

                navigatorResult?.fold(
                    onSuccess = { nav ->
                        val ttsNav = nav as? AndroidTtsNavigator ?: return
                        Log.d("TTS", "after createNavigator, currentLocator=${ttsNav.currentLocator.value}")

                        initialLocator?.let { loc ->
                            val goResult = ttsNav.go(loc)
                            Log.d("TTS", "called go(), returned=$goResult")
                            Log.d("TTS", "after go(), currentLocator=${ttsNav.currentLocator.value}")
                        }

                        Log.d("TTS", "before openSession, playback=${ttsNav.playback.value}")


                        ttsManager.openSession(bookId, ttsNav)

                        // 3️⃣ after session is opened
                        val sess = ttsManager.session.filterNotNull().first()
                        Log.d("TTS", "session opened, player=${sess.mediaSession.player}")

                        // subscribe to flows
                        ttsNav.currentLocator
                            .onEach { Log.d("TTS", "currentLocator flow: $it") }
                            .launchIn(CoroutineScope(Dispatchers.Main))

                        ttsNav.location
                            .onEach { Log.d("TTS", "location flow: $it") }
                            .launchIn(CoroutineScope(Dispatchers.Main))

                        ttsNav.playback
                            .onEach { Log.d("TTS", "playback flow: $it") }
                            .launchIn(CoroutineScope(Dispatchers.Main))

                        ttsNav.play()
                    },
                    onFailure = { error ->
                        Log.e("TtsController", "Failed to create TtsNavigator: $error")
                        // TODO: Emit a user-facing error event.
                    }
                )
            },
            onFailure = {
                Log.e("TtsController", "Failed to get publication for book $bookId", it)
                // TODO: Emit a user-facing error event.
            }
        )
    }
}