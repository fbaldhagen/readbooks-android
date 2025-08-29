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
import androidx.fragment.app.viewModels
import com.fbaldhagen.readbooks.ui.settings.ReaderSettingsContent
import com.fbaldhagen.readbooks.ui.settings.SettingsViewModel
import com.fbaldhagen.readbooks.ui.theme.ReadBooksTheme
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReaderSettingsDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val settings by viewModel.settings.collectAsState()

                ReadBooksTheme(appTheme = settings.theme) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        ReaderSettingsContent(
                            settings = settings,
                            onThemeChange = viewModel::setTheme,
                            onFontSizeChange = viewModel::setFontSize,
                            onPaddingChange = viewModel::setPagePadding
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "ReaderSettingsDialog"
    }
}