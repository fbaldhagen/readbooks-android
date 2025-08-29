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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.core.view.ViewCompat
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

        binding.settingsButton.setOnClickListener {
            showSettingsDialog()
        }

        binding.bookmarkButton.setOnClickListener {
            viewModel.toggleBookmark()
        }

        binding.tocButton.setOnClickListener {
            showTableOfContentsDialog()
        }

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
                viewModel.state.collectLatest { state ->
                    updateSystemUi(state.isSystemUiVisible)
                }
            }
        }
    }

    private fun updateSystemUi(isVisible: Boolean) {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (isVisible) {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        } else {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding.readerActionsLayout.visibility = if (isVisible) View.VISIBLE else View.GONE
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