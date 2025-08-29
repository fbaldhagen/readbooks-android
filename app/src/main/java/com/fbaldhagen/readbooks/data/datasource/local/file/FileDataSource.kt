package com.fbaldhagen.readbooks.data.datasource.local.file

import android.net.Uri
import java.io.File

interface FileDataSource {

    suspend fun copyFileFromUri(uri: Uri): File
}