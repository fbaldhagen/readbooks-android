package com.fbaldhagen.readbooks.ui.reader

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import com.fbaldhagen.readbooks.R
import com.fbaldhagen.readbooks.databinding.FragmentReaderBinding
import com.fbaldhagen.readbooks.domain.model.TtsPlaybackState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.readium.r2.navigator.Decoration
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.input.InputListener
import org.readium.r2.navigator.input.TapEvent
import org.readium.r2.navigator.preferences.Theme
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.util.AbsoluteUrl

@UnstableApi
@OptIn(ExperimentalReadiumApi::class)
@AndroidEntryPoint
class ReaderFragment : Fragment(R.layout.fragment_reader) {

    private var _binding: FragmentReaderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReaderViewModel by viewModels({ requireActivity() })

    val navigator: EpubNavigatorFragment?
        get() = childFragmentManager.findFragmentByTag(NAVIGATOR_TAG) as? EpubNavigatorFragment

    private val ttsGroup = "tts-highlight-group"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReaderBinding.bind(view)

        observeUiState()
        observeEvents()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    binding.progressBar.isVisible = state.isLoading
                    binding.errorText.isVisible = state.error != null
                    binding.errorText.text = state.error

                    if (state.navigatorFactory != null && navigator == null) {
                        attachNavigatorFragment(state)
                    }

                    navigator?.submitPreferences(state.preferences)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .map { it.ttsPlaybackState }
                    .distinctUntilChanged()
                    .collect { playbackState ->
                        if (playbackState != TtsPlaybackState.PLAYING) {
                            navigator?.applyDecorations(emptyList(), group = ttsGroup)
                        }
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .map { it.preferences.theme }
                    .distinctUntilChanged()
                    .collect { theme ->
                        theme?.let {
                            val colorResId = mapReadiumThemeToColorRes(it)
                            val colorInt = ContextCompat.getColor(requireContext(), colorResId)
                            binding.root.setBackgroundColor(colorInt)
                        }
                    }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is ReaderEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                        is ReaderEvent.GoTo -> {
                            navigator?.go(event.locator, animated = true)
                            (parentFragmentManager.findFragmentByTag(ReaderDetailsDialogFragment.TAG) as? ReaderDetailsDialogFragment)?.dismiss()
                        }

                        is ReaderEvent.HighlightTtsUtterance -> {
                            highlightUtterance(event.locator)
                        }

                        is ReaderEvent.RequestTtsPermission -> { /* No-op */ }
                    }
                }
            }
        }
    }

    private suspend fun highlightUtterance(locator: Locator) {
        val highlightColor = ContextCompat.getColor(requireContext(), R.color.color_accent_highlight)

        val decoration = Decoration(
            id = "tts-utterance-highlight",
            locator = locator,
            style = Decoration.Style.Highlight(tint = highlightColor)
        )

        navigator?.applyDecorations(listOf(decoration), group = ttsGroup)
    }

    @ColorRes
    private fun mapReadiumThemeToColorRes(theme: Theme): Int {
        return when (theme) {
            Theme.LIGHT -> R.color.readium_theme_background_light
            Theme.DARK -> R.color.readium_theme_background_dark
            Theme.SEPIA -> R.color.readium_theme_background_sepia
        }
    }

    private fun attachNavigatorFragment(state: ReaderState) {
        val factory = state.navigatorFactory ?: return
        childFragmentManager.fragmentFactory = factory.createFragmentFactory(
            initialLocator = state.initialLocator,
            initialPreferences = state.preferences,
            listener = navigatorListener,
            paginationListener = paginationListener
        )
        childFragmentManager.commit {
            replace(R.id.reader_container, EpubNavigatorFragment::class.java, null, NAVIGATOR_TAG)
            setReorderingAllowed(true)
        }
        childFragmentManager.executePendingTransactions()
        navigator?.addInputListener(inputListener)
    }

    private val paginationListener = object : EpubNavigatorFragment.PaginationListener {
        override fun onPageChanged(pageIndex: Int, totalPages: Int, locator: Locator) {
            viewModel.onLocationChanged(locator)
        }
    }

    private val inputListener = object : InputListener {
        override fun onTap(event: TapEvent): Boolean {
            viewModel.toggleSystemUi()
            return true
        }
    }

    private val navigatorListener = object : EpubNavigatorFragment.Listener {
        override fun onExternalLinkActivated(url: AbsoluteUrl) { /* TODO */ }
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveProgress()
    }

    override fun onDestroyView() {
        if (isRemoving || requireActivity().isFinishing) {
            childFragmentManager.fragmentFactory = androidx.fragment.app.FragmentFactory()
        }
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val NAVIGATOR_TAG = "EpubNavigatorFragment"
    }
}