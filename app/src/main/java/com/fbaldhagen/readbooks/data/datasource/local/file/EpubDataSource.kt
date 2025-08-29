package com.fbaldhagen.readbooks.data.datasource.local.file

import org.readium.r2.shared.publication.Publication

interface EpubDataSource {

    suspend fun openEpub(filePath: String): Result<Publication>
}