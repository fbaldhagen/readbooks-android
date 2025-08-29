package com.fbaldhagen.readbooks.domain.usecase

import android.content.Context
import androidx.work.*
import com.fbaldhagen.readbooks.data.worker.DownloadWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject

class DownloadBookUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    operator fun invoke(
        remoteId: String,
        epubUrl: String,
        title: String,
        author: String?,
        description: String?
    ): UUID {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = workDataOf(
            DownloadWorker.KEY_REMOTE_ID to remoteId,
            DownloadWorker.KEY_EPUB_URL to epubUrl,
            DownloadWorker.KEY_TITLE to title,
            DownloadWorker.KEY_AUTHOR to author,
            DownloadWorker.KEY_DESCRIPTION to description
        )

        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(
            "download_$remoteId",
            ExistingWorkPolicy.KEEP,
            downloadWorkRequest
        )

        return downloadWorkRequest.id
    }
}