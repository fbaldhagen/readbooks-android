package com.fbaldhagen.readbooks.data.datasource.local.file

import org.readium.r2.shared.publication.Publication
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.toUrl

import java.io.File
import javax.inject.Inject

class ReadiumEpubDataSource @Inject constructor(
    private val assetRetriever: AssetRetriever,
    private val publicationOpener: PublicationOpener
) : EpubDataSource {

    override suspend fun openEpub(filePath: String): Result<Publication> {
        val bookUrl = File(filePath).toUrl()
        val asset = assetRetriever.retrieve(bookUrl).getOrElse { error ->
            return Result.failure(Exception("Failed to retrieve asset: $error"))
        }

        val publicationTry = publicationOpener.open(
            asset = asset,
            allowUserInteraction = false
        )

        return publicationTry.fold(
            onSuccess = { publication -> Result.success(publication) },
            onFailure = { openError -> Result.failure(Exception("Failed to open publication: $openError")) }
        )
    }
}