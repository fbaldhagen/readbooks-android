package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.SessionRepository
import javax.inject.Inject

class StartReadingSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke(bookId: Long) {
        sessionRepository.startSession(bookId)
    }
}