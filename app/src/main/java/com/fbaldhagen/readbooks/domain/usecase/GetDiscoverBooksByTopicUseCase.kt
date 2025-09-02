package com.fbaldhagen.readbooks.domain.usecase

import androidx.paging.PagingData
import com.fbaldhagen.readbooks.domain.model.DiscoverBook
import com.fbaldhagen.readbooks.domain.repository.DiscoverRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDiscoverBooksByTopicUseCase @Inject constructor(
    private val discoverRepository: DiscoverRepository
) {
    operator fun invoke(topic: String): Flow<PagingData<DiscoverBook>> {
        return discoverRepository.getBooksByTopic(topic)
    }
}