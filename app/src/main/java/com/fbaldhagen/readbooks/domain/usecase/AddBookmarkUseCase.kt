package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.BookmarkRepository
import org.readium.r2.shared.publication.Locator
import javax.inject.Inject

class AddBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(bookId: Long, locator: Locator) =
        bookmarkRepository.addBookmark(bookId, locator)
}