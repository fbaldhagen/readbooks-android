package com.fbaldhagen.readbooks.domain.usecase

import android.net.Uri
import com.fbaldhagen.readbooks.data.datasource.local.file.FileDataSource
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import javax.inject.Inject

class ImportBookUseCase @Inject constructor(
    private val fileDataSource: FileDataSource,
    private val bookRepository: BookRepository
) {

    suspend operator fun invoke(
        uri: Uri,
        remoteId: String? = null,
        title: String? = null,
        author: String? = null,
        description: String? = null
    ): Result<Long> {
        return try {
            val internalFile = fileDataSource.copyFileFromUri(uri)
            bookRepository.addRemoteBookToLibrary(
                filePath = internalFile.absolutePath,
                remoteId = remoteId,
                title = title,
                author = author,
                description = description
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}