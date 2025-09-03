package com.fbaldhagen.readbooks.ui.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fbaldhagen.readbooks.R
import com.fbaldhagen.readbooks.databinding.ActivityReaderHostBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.readium.r2.navigator.preferences.Theme

@AndroidEntryPoint
class ReaderActivity : AppCompatActivity() {

    private val viewModel: ReaderViewModel by viewModels()
    private lateinit var binding: ActivityReaderHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityReaderHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleWindowInsets()
        binding.settingsButton.setOnClickListener { showSettingsDialog() }
        binding.bookmarkButton.setOnClickListener { viewModel.toggleBookmark() }
        binding.tocButton.setOnClickListener { showTableOfContentsDialog() }
        binding.readAloudButton.setOnClickListener { viewModel.onTopBarTtsButtonClicked() }
        binding.ttsPlayPauseButton.setOnClickListener { viewModel.onTtsPlayPauseClicked() }
        binding.ttsStopButton.setOnClickListener { viewModel.onTtsStopClicked() }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_host_container, ReaderFragment::class.java, null)
            }
        }
        observeSystemUiVisibility()
        observeTheme()
        observeBookmarkState()
    }

    private fun showSettingsDialog() {
        if (isFinishing || isDestroyed) {
            return
        }

        if (supportFragmentManager.findFragmentByTag(ReaderSettingsDialogFragment.TAG) == null) {
            ReaderSettingsDialogFragment().show(supportFragmentManager, ReaderSettingsDialogFragment.TAG)
        }
    }

    private fun showTableOfContentsDialog() {
        if (isFinishing || isDestroyed) return
        if (supportFragmentManager.findFragmentByTag(ReaderDetailsDialogFragment.TAG) == null) {
            ReaderDetailsDialogFragment().show(supportFragmentManager, ReaderDetailsDialogFragment.TAG)
        }
    }

    private fun handleWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.readerActionsLayout) { view, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val params = view.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = systemBarInsets.top + (8 * resources.displayMetrics.density).toInt()
            view.layoutParams = params

            insets
        }
    }

    private fun observeSystemUiVisibility() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    updateSystemUi(state)
                }
            }
        }
    }

    private fun updateSystemUi(state: ReaderState) {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (state.isSystemUiVisible) {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        } else {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        binding.readerActionsLayout.isVisible = state.isSystemUiVisible

        val isTtsSessionActive = state.ttsPlaybackState != TtsPlaybackState.IDLE &&
                state.ttsPlaybackState != TtsPlaybackState.FINISHED &&
                state.ttsPlaybackState != TtsPlaybackState.ERROR

        binding.ttsControlsLayout.isVisible = state.isSystemUiVisible && isTtsSessionActive

        if (isTtsSessionActive) {
            binding.readAloudButton.setImageResource(R.drawable.ic_placeholder_book)
            binding.readAloudButton.contentDescription = getString(R.string.return_to_reading_mode)
        } else {
            binding.readAloudButton.setImageResource(R.drawable.ic_read_aloud)
            binding.readAloudButton.contentDescription = getString(R.string.read_aloud)
        }

        when (state.ttsPlaybackState) {
            TtsPlaybackState.BUFFERING -> {
                binding.ttsPlayPauseButton.visibility = View.INVISIBLE
                binding.ttsBufferingSpinner.visibility = View.VISIBLE
            }
            TtsPlaybackState.PLAYING -> {
                binding.ttsPlayPauseButton.visibility = View.VISIBLE
                binding.ttsBufferingSpinner.visibility = View.GONE
                binding.ttsPlayPauseButton.setImageResource(R.drawable.ic_pause)
                binding.ttsPlayPauseButton.contentDescription = getString(R.string.pause_reading_aloud)
            }
            TtsPlaybackState.PAUSED, TtsPlaybackState.IDLE, TtsPlaybackState.FINISHED, TtsPlaybackState.ERROR -> {
                binding.ttsPlayPauseButton.visibility = View.VISIBLE
                binding.ttsBufferingSpinner.visibility = View.GONE
                binding.ttsPlayPauseButton.setImageResource(R.drawable.ic_play)
                binding.ttsPlayPauseButton.contentDescription = getString(R.string.play_reading_aloud)
            }
        }
    }

    private fun observeTheme() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.map { it.preferences.theme }.distinctUntilChanged().collect { theme ->
                    val controller = WindowCompat.getInsetsController(window, window.decorView)
                    controller.isAppearanceLightStatusBars = (theme == Theme.LIGHT || theme == Theme.SEPIA)
                }
            }
        }
    }

    private fun observeBookmarkState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.map { it.isCurrentPageBookmarked }.distinctUntilChanged().collect { isBookmarked ->
                    updateBookmarkButton(isBookmarked)
                }
            }
        }
    }

    private fun updateBookmarkButton(isBookmarked: Boolean) {
        if (isBookmarked) {
            binding.bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled)
            binding.bookmarkButton.contentDescription = getString(R.string.remove_bookmark)
        } else {
            binding.bookmarkButton.setImageResource(R.drawable.ic_bookmark_border)
            binding.bookmarkButton.contentDescription = getString(R.string.add_bookmark)
        }
    }

    companion object {
        private const val EXTRA_BOOK_ID = "bookId"
        const val EXTRA_HREF = "href"

        fun createIntent(context: Context, bookId: Long, href: String? = null): Intent =
            Intent(context, ReaderActivity::class.java).apply {
                putExtra(EXTRA_BOOK_ID, bookId)
                href?.let { putExtra(EXTRA_HREF, it) }
            }
    }
}