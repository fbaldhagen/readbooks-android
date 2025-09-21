package com.fbaldhagen.readbooks.domain.manager

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.fbaldhagen.readbooks.service.TtsService
import kotlinx.coroutines.flow.StateFlow
import org.readium.navigator.media.tts.AndroidTtsNavigator
import org.readium.r2.shared.ExperimentalReadiumApi

@OptIn(UnstableApi::class)
interface TtsManager {
    /**
     * A flow representing the current TTS session.
     * Null if no TTS is active.
     */

    val session: StateFlow<TtsService.Session?>

    /**
     * Opens a new TTS session with the given navigator.
     * This will stop any existing session.
     */

    @kotlin.OptIn(ExperimentalReadiumApi::class)
    suspend fun openSession(bookId: Long, navigator: AndroidTtsNavigator)

    /**
     * Closes the current TTS session.
     */
    fun closeSession()
}