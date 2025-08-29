package com.fbaldhagen.readbooks.ui.reader

import com.fbaldhagen.readbooks.domain.model.Bookmark
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Link
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication

data class ReaderState @OptIn(ExperimentalReadiumApi::class) constructor(
    val isLoading: Boolean = true,
    val initialLocator: Locator? = null,
    val preferences: EpubPreferences = EpubPreferences(),
    val error: String? = null,
    val navigatorFactory: EpubNavigatorFactory? = null,
    val navigatorConfiguration: EpubNavigatorFragment.Configuration? = null,
    val publication: Publication? = null,
    val isSystemUiVisible: Boolean = true,
    val bookmarks: List<Bookmark> = emptyList(),
    val isCurrentPageBookmarked: Boolean = false,
    val tableOfContents: List<Link> = emptyList()
)

sealed interface ReaderEvent {
    data class ShowToast(val message: String) : ReaderEvent
    data class GoTo(val locator: Locator) : ReaderEvent
}