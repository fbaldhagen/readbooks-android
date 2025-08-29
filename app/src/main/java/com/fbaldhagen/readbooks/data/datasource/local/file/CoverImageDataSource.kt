package com.fbaldhagen.readbooks.data.datasource.local.file

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoverImageDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun saveCoverImage(imageData: ByteArray): File = withContext(Dispatchers.IO) {
        val coversDir = File(context.filesDir, "covers")
        if (!coversDir.exists()) {
            coversDir.mkdirs()
        }
        val coverFile = File(coversDir, "${UUID.randomUUID()}.jpg")
        coverFile.writeBytes(imageData)
        coverFile
    }
}