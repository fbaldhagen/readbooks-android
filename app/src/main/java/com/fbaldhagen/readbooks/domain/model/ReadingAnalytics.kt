package com.fbaldhagen.readbooks.domain.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO

data class ReadingAnalytics(
    val totalReadingTime: Duration = ZERO,
    val longestStreakInDays: Int = 0,
    val averageSessionDuration: Duration = ZERO
)