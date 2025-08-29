package com.fbaldhagen.readbooks.domain.model

data class ReadingSession(
    val id: Long,
    val bookId: Long,
    val startTimeMillis: Long,
    val endTimeMillis: Long? = null
)