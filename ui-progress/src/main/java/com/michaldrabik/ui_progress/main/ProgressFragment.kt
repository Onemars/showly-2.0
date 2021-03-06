package com.michaldrabik.ui_progress.main

import android.annotation.SuppressLint
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.updateMargins
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.michaldrabik.ui_base.BaseFragment
import com.michaldrabik.ui_base.common.OnScrollResetListener
import com.michaldrabik.ui_base.common.OnTabReselectedListener
import com.michaldrabik.ui_base.common.OnTraktSyncListener
import com.michaldrabik.ui_base.common.views.exSearchViewIcon
import com.michaldrabik.ui_base.common.views.exSearchViewInput
import com.michaldrabik.ui_base.common.views.exSearchViewText
import com.michaldrabik.ui_base.utilities.extensions.dimenToPx
import com.michaldrabik.ui_base.utilities.extensions.doOnApplyWindowInsets
import com.michaldrabik.ui_base.utilities.extensions.fadeOut
import com.michaldrabik.ui_base.utilities.extensions.gone
import com.michaldrabik.ui_base.utilities.extensions.hideKeyboard
import com.michaldrabik.ui_base.utilities.extensions.nextPage
import com.michaldrabik.ui_base.utilities.extensions.onClick
import com.michaldrabik.ui_base.utilities.extensions.showKeyboard
import com.michaldrabik.ui_base.utilities.extensions.visible
import com.michaldrabik.ui_base.utilities.extensions.visibleIf
import com.michaldrabik.ui_episodes.details.EpisodeDetailsBottomSheet
import com.michaldrabik.ui_model.Episode
import com.michaldrabik.ui_model.IdTrakt
import com.michaldrabik.ui_model.SortOrder
import com.michaldrabik.ui_model.SortOrder.EPISODES_LEFT
import com.michaldrabik.ui_model.SortOrder.NAME
import com.michaldrabik.ui_model.SortOrder.RECENTLY_WATCHED
import com.michaldrabik.ui_navigation.java.NavigationArgs.ARG_SHOW_ID
import com.michaldrabik.ui_progress.ProgressItem
import com.michaldrabik.ui_progress.R
import com.michaldrabik.ui_progress.di.UiProgressComponentProvider
import kotlinx.android.synthetic.main.fragment_progress.*

class ProgressFragment :
  BaseFragment<ProgressViewModel>(R.layout.fragment_progress),
  OnEpisodesSyncedListener,
  OnTabReselectedListener,
  OnTraktSyncListener,
  TabLayout.OnTabSelectedListener {

  override val viewModel by viewModels<ProgressViewModel> { viewModelFactory }

  private var searchViewTranslation = 0F
  private var tabsTranslation = 0F
  private var sortIconTranslation = 0F

  override fun onCreate(savedInstanceState: Bundle?) {
    (requireActivity() as UiProgressComponentProvider).provideProgressComponent().inject(this)
    super.onCreate(savedInstanceState)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupView()
    setupPager()
    setupStatusBar()

    viewModel.run {
      uiLiveData.observe(viewLifecycleOwner, { render(it!!) })
      messageLiveData.observe(viewLifecycleOwner, { showSnack(it) })
    }
  }

  override fun onResume() {
    super.onResume()
    setupBackPressed()
    showNavigation()
    viewModel.loadWatchlist()
  }

  override fun onDestroyView() {
    progressTabs.removeOnTabSelectedListener(this)
    super.onDestroyView()
  }

  private fun setupView() {
    progressSearchView.run {
      hint = getString(R.string.textSearchWatchlist)
      settingsIconVisible = true
      isClickable = false
      onClick { enterSearch() }
      onSettingsClickListener = { openSettings() }
    }

    progressTabs.translationY = tabsTranslation
    progressSearchView.translationY = searchViewTranslation
    progressSortIcon.translationY = sortIconTranslation
  }

  @SuppressLint("WrongConstant")
  private fun setupPager() {
    progressPager.run {
      offscreenPageLimit = ProgressPagesAdapter.PAGES_COUNT
      isUserInputEnabled = false
      adapter = ProgressPagesAdapter(this@ProgressFragment)
    }

    TabLayoutMediator(progressTabs, progressPager) { tab, position ->
      tab.text = when (position) {
        0 -> getString(R.string.tabWatchlist)
        else -> getString(R.string.tabCalendar)
      }
    }.attach()

    progressTabs.addOnTabSelectedListener(this)
  }

  private fun setupStatusBar() {
    progressRoot.doOnApplyWindowInsets { _, insets, _, _ ->
      val statusBarSize = insets.systemWindowInsetTop
      (progressSearchView.layoutParams as ViewGroup.MarginLayoutParams)
        .updateMargins(top = statusBarSize + dimenToPx(R.dimen.spaceSmall))
      (progressTabs.layoutParams as ViewGroup.MarginLayoutParams)
        .updateMargins(top = statusBarSize + dimenToPx(R.dimen.progressSearchViewPadding))
      (progressSortIcon.layoutParams as ViewGroup.MarginLayoutParams)
        .updateMargins(top = statusBarSize + dimenToPx(R.dimen.progressSearchViewPadding))
    }
  }

  private fun setupBackPressed() {
    val dispatcher = requireActivity().onBackPressedDispatcher
    dispatcher.addCallback(viewLifecycleOwner) {
      if (progressSearchView.isSearching) {
        exitSearch()
      } else {
        remove()
        dispatcher.onBackPressed()
      }
    }
  }

  override fun onTabSelected(tab: TabLayout.Tab) {
    progressPager.currentItem = tab.position
  }

  fun openShowDetails(item: ProgressItem) {
    viewModel.onOpenShowDetails()
    exitSearch()
    hideNavigation()
    saveUiTranslations()
    progressRoot.fadeOut {
      val bundle = Bundle().apply { putLong(ARG_SHOW_ID, item.show.ids.trakt.id) }
      navigateTo(R.id.actionProgressFragmentToShowDetailsFragment, bundle)
    }
  }

  private fun openSettings() {
    hideNavigation()
    navigateTo(R.id.actionProgressFragmentToSettingsFragment)
    saveUiTranslations()
  }

  fun openTraktSync() {
    navigateTo(R.id.actionProgressFragmentToTraktSyncFragment)
    hideNavigation()
    saveUiTranslations()
  }

  fun openEpisodeDetails(showId: IdTrakt, episode: Episode) {
    val modal = EpisodeDetailsBottomSheet.create(
      showId,
      episode,
      isWatched = false,
      showButton = false
    )
    modal.show(requireActivity().supportFragmentManager, "MODAL")
  }

  private fun openSortOrderDialog(order: SortOrder) {
    val options = listOf(NAME, RECENTLY_WATCHED, EPISODES_LEFT)
    val optionsStrings = options.map { getString(it.displayString) }.toTypedArray()

    MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog)
      .setTitle(R.string.textSortBy)
      .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_dialog))
      .setSingleChoiceItems(optionsStrings, options.indexOf(order)) { dialog, index ->
        viewModel.setSortOrder(options[index])
        dialog.dismiss()
      }
      .show()
  }

  private fun saveUiTranslations() {
    tabsTranslation = progressTabs.translationY
    searchViewTranslation = progressSearchView.translationY
    sortIconTranslation = progressSortIcon.translationY
  }

  private fun enterSearch() {
    if (progressSearchView.isSearching) return
    progressSearchView.isSearching = true
    exSearchViewText.gone()
    exSearchViewInput.run {
      setText("")
      doAfterTextChanged { viewModel.searchWatchlist(it?.toString() ?: "") }
      visible()
      showKeyboard()
      requestFocus()
    }
    (exSearchViewIcon.drawable as Animatable).start()
    exSearchViewIcon.onClick { exitSearch() }
    hideNavigation(false)
  }

  private fun exitSearch(showNavigation: Boolean = true) {
    progressSearchView.isSearching = false
    exSearchViewText.visible()
    exSearchViewInput.run {
      setText("")
      gone()
      hideKeyboard()
      clearFocus()
    }
    exSearchViewIcon.setImageResource(R.drawable.ic_anim_search_to_close)
    if (showNavigation) showNavigation()
  }

  override fun onEpisodesSyncFinished() = viewModel.loadWatchlist()

  override fun onTraktSyncProgress() = viewModel.loadWatchlist()

  override fun onTabReselected() {
    progressSearchView.translationY = 0F
    progressTabs.translationY = 0F
    progressSortIcon.translationY = 0F
    progressPager.nextPage()
    childFragmentManager.fragments.forEach {
      (it as? OnScrollResetListener)?.onScrollReset()
    }
  }

  fun resetTranslations() {
    progressSearchView.animate().translationY(0F).start()
    progressTabs.animate().translationY(0F).start()
    progressSortIcon.animate().translationY(0F).start()
  }

  private fun render(uiModel: ProgressUiModel) {
    uiModel.run {
      items?.let {
        progressSearchView.isClickable = it.isNotEmpty() || isSearching == true
        progressSortIcon.visibleIf(it.isNotEmpty())
        if (it.isNotEmpty() && sortOrder != null) {
          progressSortIcon.onClick { openSortOrderDialog(sortOrder) }
        }
      }
    }
  }

  override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
  override fun onTabReselected(tab: TabLayout.Tab?) = Unit
}
