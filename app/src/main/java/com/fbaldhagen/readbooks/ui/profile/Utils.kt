package com.fbaldhagen.readbooks.ui.profile

import kotlin.time.Duration.Companion.hours

fun formatDuration(duration: kotlin.time.Duration): String {
    if (duration <= kotlin.time.Duration.ZERO) return "0m"

    val hours = duration.inWholeHours
    val minutes = (duration - hours.hours).inWholeMinutes

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
}