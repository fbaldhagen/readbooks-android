package com.fbaldhagen.readbooks.ui.library

import com.fbaldhagen.readbooks.domain.model.ReadingStatus

fun LibraryFilter.toDisplayName(): String {
    return when (this) {
        is LibraryFilter.All -> "All"
        is LibraryFilter.ByStatus -> when (this.status) {
            ReadingStatus.NOT_STARTED -> "Unread"
            ReadingStatus.IN_PROGRESS -> "In Progress"
            ReadingStatus.FINISHED -> "Finished"
        }
    }
}