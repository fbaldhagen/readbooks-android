package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.model.BookDetails
import com.fbaldhagen.readbooks.domain.repository.BookRepository
import com.fbaldhagen.readbooks.domain.repository.DiscoverRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBookDetailsUseCase @Inject constructor(
    private val localBookRepository: BookRepository,
    private val remoteBookRepository: DiscoverRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(localId: Long?, remoteId: String?): Flow<Result<BookDetails>> {
        return when {
            localId != null -> {
                localBookRepository.getBookDetails(localId)
                    .map { bookDetails -> Result.success(bookDetails) }
            }

            remoteId != null -> {
                localBookRepository.getBookStreamByRemoteId(remoteId)
                    .flatMapLatest { localBook ->
                        if (localBook != null) {
                            flow { emit(Result.success(localBook)) }
                        } else {
                            flow {
                                val remoteBook = remoteBookRepository.getRemoteBookDetails(remoteId)
                                emit(Result.success(remoteBook))
                            }
                        }
                    }
            }

            else -> {
                flow { emit(Result.failure(IllegalArgumentException("Either a local or remote ID must be provided."))) }
            }
        }.catch { e ->
            emit(Result.failure(e))
        }
    }
}