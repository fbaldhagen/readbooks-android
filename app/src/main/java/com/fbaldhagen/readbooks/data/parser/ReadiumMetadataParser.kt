package com.fbaldhagen.readbooks.data.parser


import com.fbaldhagen.readbooks.data.datasource.local.file.CoverImageDataSource
import com.fbaldhagen.readbooks.data.datasource.local.file.EpubDataSource
import com.fbaldhagen.readbooks.domain.model.Book
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.parser.EpubMetadataParser
import org.readium.r2.shared.publication.Publication
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadiumMetadataParser @Inject constructor(
    private val epubDataSource: EpubDataSource,
    private val coverImageDataSource: CoverImageDataSource
) : EpubMetadataParser {

    override suspend fun parse(file: File): Result<Book> {
        val publicationResult = epubDataSource.openEpub(file.absolutePath)

        return publicationResult.map { publication ->
            val coverImagePath = extractAndSaveCover(publication)

            Book(
                title = publication.metadata.title ?: "",
                author = publication.metadata.authors.joinToString(", ") { it.name }.ifEmpty { null },
                description = publication.metadata.description,
                filePath = file.absolutePath,
                coverImagePath = coverImagePath,
                dateAdded = System.currentTimeMillis(),
                readingStatus = ReadingStatus.NOT_STARTED
            )
        }
    }

    private suspend fun extractAndSaveCover(publication: Publication): String? {
        val coverLink = publication.linkWithRel("cover") ?: return null

        val coverBytesResult = publication.get(coverLink)?.read()

        return coverBytesResult?.fold(
            onSuccess = { bytes ->
                val coverFile = coverImageDataSource.saveCoverImage(bytes)
                coverFile.absolutePath
            },
            onFailure = {
                null
            }
        )
    }
}