package com.fbaldhagen.readbooks.domain.model

data class FilterState(
    val statuses: Set<ReadingStatus> = emptySet()
) {
    companion object {
        val default = FilterState()
    }

    val isActive: Boolean
        get() = statuses.isNotEmpty()
}