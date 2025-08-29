package com.fbaldhagen.readbooks.domain.model

enum class SortType(val displayName: String) {
    TITLE_ASC("Title (A-Z)"),
    AUTHOR_ASC("Author (A-Z)"),
    DATE_ADDED_DESC("Date Added (Newest)"),
    LAST_READ_DESC("Last Read (Recent)")
}