package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.BookRepository
import javax.inject.Inject

class SeedLibraryFromAssetsUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return bookRepository.seedLibraryFromAssets()
    }
}