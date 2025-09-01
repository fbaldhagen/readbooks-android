package com.fbaldhagen.readbooks.ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue

sealed interface TopBarState {
    data class Standard(
        val title: String? = null,
        val actions: @Composable RowScope.() -> Unit = {}
    ) : TopBarState

    data class Detail(
        val title: String
    ) : TopBarState

    data class Search(
        val query: TextFieldValue,
        val onQueryChange: (TextFieldValue) -> Unit,
        val onClose: () -> Unit,
        val hint: String = "Search books and authors..."
    ) : TopBarState
}