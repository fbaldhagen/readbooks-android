package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.BookmarkRepository
import javax.inject.Inject

class GetBookmarksForBookUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    operator fun invoke(bookId: Long) = bookmarkRepository.getBookmarksForBook(bookId)
}