package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.AchievementId
import com.fbaldhagen.readbooks.domain.model.ReadingStatus
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import javax.inject.Inject

class UpdateReadingStatusUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val updateAchievementProgressUseCase: UpdateAchievementProgressUseCase
) {
    suspend operator fun invoke(bookId: Long, newStatus: ReadingStatus) {
        val currentBook = bookRepository.getBookById(bookId) ?: return
        val oldStatus = currentBook.readingStatus

        if (oldStatus == newStatus) {
            return
        }

        when {
            oldStatus != ReadingStatus.FINISHED && newStatus == ReadingStatus.FINISHED -> {
                updateAchievementProgressUseCase(AchievementId.BOOKWORM, progressToAdd = 1)
            }
            oldStatus == ReadingStatus.FINISHED -> {
                updateAchievementProgressUseCase(AchievementId.BOOKWORM, progressToAdd = -1)
            }
        }

        bookRepository.updateReadingStatus(bookId, newStatus)
    }
}