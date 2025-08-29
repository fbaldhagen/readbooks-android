package com.fbaldhagen.readbooks.data.datasource.local.file

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FileDataSource {

    override suspend fun copyFileFromUri(uri: Uri): File = withContext(Dispatchers.IO) {
        val destinationDir = File(context.filesDir, "epubs")
        if (!destinationDir.exists()) {
            destinationDir.mkdirs()
        }

        val fileName = "${UUID.randomUUID()}.epub"
        val destinationFile = File(destinationDir, fileName)

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw IOException("Failed to open input stream for URI: $uri")
        } catch (e: Exception) {
            if (destinationFile.exists()) {
                destinationFile.delete()
            }
            throw IOException("Failed to copy file from URI: $uri", e)
        }

        destinationFile
    }
}