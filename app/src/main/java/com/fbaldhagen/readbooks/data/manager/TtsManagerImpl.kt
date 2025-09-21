package com.fbaldhagen.readbooks.data.manager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.fbaldhagen.readbooks.domain.manager.TtsManager
import com.fbaldhagen.readbooks.service.TtsService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import org.readium.navigator.media.tts.AndroidTtsNavigator
import org.readium.r2.shared.ExperimentalReadiumApi
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@UnstableApi
@Singleton
class TtsManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TtsManager, ServiceConnection {

    private val serviceAction = "com.fbaldhagen.readbooks.service.TTS_SERVICE_INTERFACE"
    private val coroutineScope: CoroutineScope = MainScope()
    private val binder: MutableStateFlow<TtsService.Binder?> = MutableStateFlow(null)

    override val session: StateFlow<TtsService.Session?> =
        binder.flatMapLatest { serviceBinder ->
            serviceBinder?.session ?: flowOf(null)
        }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        val intent = Intent(serviceAction).apply { setClass(context, TtsService::class.java) }
        context.startService(intent)
        context.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d("TtsManagerImpl", "TtsService connected")
        this.binder.value = service as TtsService.Binder
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d("TtsManagerImpl", "TtsService disconnected")
        this.binder.value = null
    }

    @OptIn(ExperimentalReadiumApi::class)
    override suspend fun openSession(bookId: Long, navigator: AndroidTtsNavigator) {
        val serviceBinder = binder.filterNotNull().first()
        serviceBinder.openSession(navigator = navigator, bookId = bookId)
    }

    override fun closeSession() {
        binder.value?.closeSession()
    }
}