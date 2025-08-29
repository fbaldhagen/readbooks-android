package com.fbaldhagen.readbooks.domain.model

data class ReadingStreakInfo(
    val count: Int,
    val status: StreakStatus
)

enum class StreakStatus {
    COMPLETED_TODAY,
    IN_PROGRESS,
    INACTIVE
}