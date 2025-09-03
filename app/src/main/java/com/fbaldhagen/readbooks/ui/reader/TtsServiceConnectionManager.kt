package com.fbaldhagen.readbooks.ui.reader

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val TAG = "TtsServiceConnection"

class TtsServiceConnectionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _service = MutableStateFlow<TtsService?>(null)
    val service = _service.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            Log.d(TAG, "TtsService connected")
            _service.value = (binder as? TtsService.TtsBinder)?.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "TtsService disconnected")
            _service.value = null
        }
    }

    fun bind() {
        Log.d(TAG, "Attempting to bind to TtsService.")
        val intent = Intent(context, TtsService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbind() {
        if (_service.value != null) {
            Log.d(TAG, "Unbinding from TtsService.")
            context.unbindService(serviceConnection)
            _service.value = null
        }
    }
}