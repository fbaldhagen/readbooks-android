package com.fbaldhagen.readbooks.data.worker

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.fbaldhagen.readbooks.domain.usecase.ImportBookUseCase
import com.fbaldhagen.readbooks.ui.notifications.DownloadNotificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val okHttpClient: OkHttpClient,
    private val importBookUseCase: ImportBookUseCase,
    private val notificationManager: DownloadNotificationManager
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_EPUB_URL = "epub_url"
        const val KEY_REMOTE_ID = "remote_id"
        const val KEY_TITLE = "title"
        const val KEY_AUTHOR = "author"
        const val KEY_PROGRESS = "progress"
        const val KEY_DESCRIPTION = "description"
        const val KEY_LOCAL_ID = "local_id"
    }

    private val remoteId by lazy { inputData.getString(KEY_REMOTE_ID)!! }
    private val bookTitle by lazy { inputData.getString(KEY_TITLE) ?: "Downloading Book" }
    private val notificationId by lazy { remoteId.hashCode() }
    private val description by lazy { inputData.getString(KEY_DESCRIPTION)!! }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            notificationId,
            notificationManager.getInitialNotification(id, bookTitle)
        )
    }

    override suspend fun doWork(): Result {
        val epubUrl = inputData.getString(KEY_EPUB_URL)
        val author = inputData.getString(KEY_AUTHOR)

        if (epubUrl.isNullOrBlank()) {
            notificationManager.showDownloadFailed(notificationId, bookTitle)
            return Result.failure()
        }

        val tempFile = File(appContext.cacheDir, "$remoteId.epub.tmp")

        try {
            setProgress(workDataOf(KEY_PROGRESS to 0))
            notificationManager.updateProgress(id, notificationId, bookTitle, 0)

            val request = Request.Builder().url(epubUrl).build()
            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                notificationManager.showDownloadFailed(notificationId, bookTitle)
                return Result.failure()
            }

            val body = response.body ?: run {
                notificationManager.showDownloadFailed(notificationId, bookTitle)
                return Result.failure()
            }
            val contentLength = body.contentLength()
            var bytesCopied = 0L

            body.source().use { source ->
                tempFile.sink().buffer().use { sink ->
                    var read: Long
                    val buffer = okio.Buffer()
                    while (source.read(buffer, 8192L).also { read = it } != -1L) {
                        if (isStopped) {
                            throw InterruptedException("Worker was cancelled.")
                        }
                        sink.write(buffer, read)
                        bytesCopied += read
                        if (contentLength > 0) {
                            val progress = (bytesCopied * 100 / contentLength).toInt()
                            setProgress(workDataOf(KEY_PROGRESS to progress))
                            notificationManager.updateProgress(id, notificationId, bookTitle, progress)
                        }
                    }
                }
            }

            setProgress(workDataOf(KEY_PROGRESS to 100))
            notificationManager.updateProgress(id, notificationId, bookTitle, 100)

            val importResult = importBookUseCase(
                uri = Uri.fromFile(tempFile),
                remoteId = remoteId,
                title = bookTitle,
                author = author,
                description = description
            )

            return importResult.fold(
                onSuccess = { newBookId ->
                    notificationManager.showDownloadComplete(notificationId, bookTitle)

                    val outputData = workDataOf(KEY_LOCAL_ID to newBookId)
                    Result.success(outputData)
                },
                onFailure = {
                    notificationManager.showDownloadFailed(notificationId, bookTitle)
                    Result.failure()
                }
            )

        } catch (e: Exception) {
            if (isStopped) {
                // Nothing
            } else {
                notificationManager.showDownloadFailed(notificationId, bookTitle)
            }
            return Result.failure()
        } finally {
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    }
}