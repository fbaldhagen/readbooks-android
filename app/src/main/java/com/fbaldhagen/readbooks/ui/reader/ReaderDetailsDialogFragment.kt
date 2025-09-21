package com.fbaldhagen.readbooks.ui.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.media3.common.util.UnstableApi
import com.fbaldhagen.readbooks.ui.settings.SettingsViewModel
import com.fbaldhagen.readbooks.ui.theme.ReadBooksTheme
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@UnstableApi
@AndroidEntryPoint
class ReaderDetailsDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: ReaderViewModel by viewModels({ requireActivity() })
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val readerState by viewModel.state.collectAsState()
                val settingsState by settingsViewModel.settings.collectAsState()

                ReadBooksTheme(appTheme = settingsState.theme) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        ReaderDetailsScreen(
                            tableOfContents = readerState.tableOfContents,
                            bookmarks = readerState.bookmarks,
                            onChapterClicked = viewModel::goToHref,
                            onBookmarkClicked = viewModel::goToLocator,
                            onDeleteBookmark = viewModel::deleteBookmark
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "TableOfContentsDialogFragment"
    }
}