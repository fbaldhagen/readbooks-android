package com.fbaldhagen.readbooks.ui.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import com.fbaldhagen.readbooks.R
import com.fbaldhagen.readbooks.utils.NotificationConstants.DOWNLOAD_CHANNEL_ID
import com.fbaldhagen.readbooks.utils.NotificationConstants.DOWNLOAD_CHANNEL_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)
    private val workManager = WorkManager.getInstance(context)

    init {
        createNotificationChannel()
    }

    fun getInitialNotification(workId: UUID, bookTitle: String): Notification {
        return getBaseBuilder(workId, bookTitle)
            .setContentText("Download starting...")
            .setProgress(100, 0, true)
            .build()
    }

    fun updateProgress(workId: UUID, notificationId: Int, bookTitle: String, progress: Int) {
        val notification = getBaseBuilder(workId, bookTitle)
            .setContentText("Downloading...")
            .setProgress(100, progress, false)
            .build()
        safeNotify(notificationId, notification)
    }

    fun showDownloadComplete(notificationId: Int, bookTitle: String) {
        val notification = getFinalStateBuilder(bookTitle)
            .setContentTitle(bookTitle)
            .setContentText("Download complete")
            .build()
        safeNotify(notificationId, notification)
    }

    fun showDownloadFailed(notificationId: Int, bookTitle: String) {
        val notification = getFinalStateBuilder(bookTitle)
            .setContentText("Download failed")
            .build()
        safeNotify(notificationId, notification)
    }

    private fun safeNotify(id: Int, notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(id, notification)
    }

    private fun getBaseBuilder(workId: UUID, bookTitle: String): NotificationCompat.Builder {
        val cancelIntent = workManager.createCancelPendingIntent(workId)
        val cancelAction = NotificationCompat.Action.Builder(
            R.drawable.ic_close,
            "Cancel",
            cancelIntent
        ).build()

        return NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_download)
            .setContentTitle(bookTitle)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(cancelAction)
    }

    private fun getFinalStateBuilder(bookTitle: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_download)
            .setContentTitle(bookTitle)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(false) // Make it dismissible
            .setProgress(0, 0, false)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DOWNLOAD_CHANNEL_ID,
                DOWNLOAD_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for book download progress"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}