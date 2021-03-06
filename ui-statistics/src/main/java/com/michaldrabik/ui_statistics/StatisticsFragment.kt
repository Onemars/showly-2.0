package com.michaldrabik.ui_statistics

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.michaldrabik.ui_base.BaseFragment
import com.michaldrabik.ui_base.utilities.extensions.doOnApplyWindowInsets
import com.michaldrabik.ui_base.utilities.extensions.fadeIf
import com.michaldrabik.ui_base.utilities.extensions.visibleIf
import com.michaldrabik.ui_navigation.java.NavigationArgs.ARG_SHOW_ID
import com.michaldrabik.ui_statistics.di.UiStatisticsComponentProvider
import kotlinx.android.synthetic.main.fragment_statistics.*

class StatisticsFragment : BaseFragment<StatisticsViewModel>(R.layout.fragment_statistics) {

  override val viewModel by viewModels<StatisticsViewModel> { viewModelFactory }

  override fun onCreate(savedInstanceState: Bundle?) {
    (requireActivity() as UiStatisticsComponentProvider).provideStatisticsComponent().inject(this)
    super.onCreate(savedInstanceState)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupView()
    setupStatusBar()

    viewModel.run {
      uiLiveData.observe(viewLifecycleOwner, { render(it!!) })
      if (!isInitialized) {
        loadMostWatchedShows()
        isInitialized = true
      }
      loadRatings()
    }
  }

  override fun onResume() {
    super.onResume()
    hideNavigation()
    handleBackPressed()
  }

  private fun setupView() {
    statisticsToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    statisticsMostWatchedShows.run {
      onLoadMoreClickListener = { addLimit -> viewModel.loadMostWatchedShows(addLimit) }
      onShowClickListener = {
        openShowDetails(it.traktId)
      }
    }
    statisticsRatings.onShowClickListener = {
      openShowDetails(it.show.traktId)
    }
  }

  private fun setupStatusBar() {
    statisticsRoot.doOnApplyWindowInsets { view, insets, padding, _ ->
      view.updatePadding(top = padding.top + insets.systemWindowInsetTop)
    }
  }

  private fun render(uiModel: StatisticsUiModel) {
    uiModel.run {
      statisticsMostWatchedShows.bind(mostWatchedShows ?: emptyList(), mostWatchedTotalCount ?: 0)
      statisticsTotalTimeSpent.bind(totalTimeSpentMinutes ?: 0)
      statisticsTotalEpisodes.bind(totalWatchedEpisodes ?: 0, totalWatchedEpisodesShows ?: 0)
      statisticsTopGenres.bind(topGenres ?: emptyList())
      statisticsRatings.bind(ratings ?: emptyList())

      ratings?.let { statisticsRatings.visibleIf(it.isNotEmpty()) }
      mostWatchedShows?.let {
        statisticsContent.fadeIf(it.isNotEmpty())
        statisticsEmptyView.fadeIf(it.isEmpty())
      }
      archivedShowsIncluded?.let {
        if (!it) {
          statisticsToolbar.subtitle = getString(R.string.textArchivedShowsExcluded)
        }
      }
    }
  }

  private fun openShowDetails(traktId: Long) {
    val bundle = bundleOf(ARG_SHOW_ID to traktId)
    navigateTo(R.id.actionStatisticsFragmentToShowDetailsFragment, bundle)
  }

  private fun handleBackPressed() {
    val dispatcher = requireActivity().onBackPressedDispatcher
    dispatcher.addCallback(viewLifecycleOwner) {
      remove()
      showNavigation()
      findNavController().popBackStack()
    }
  }
}
