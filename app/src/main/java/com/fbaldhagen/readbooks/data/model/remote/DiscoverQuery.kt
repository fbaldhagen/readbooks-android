package com.fbaldhagen.readbooks.data.model.remote

sealed class DiscoverQuery(val cacheKey: String) {
    data class ByTopic(val topic: String) : DiscoverQuery(cacheKey = "topic_$topic")
    data class BySearch(val term: String) : DiscoverQuery(cacheKey = "search_$term")
    data object Popular : DiscoverQuery(cacheKey = "popular")
}