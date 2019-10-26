package com.michaldrabik.showly2.ui.followedshows

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.michaldrabik.showly2.model.ImageType
import com.michaldrabik.showly2.ui.common.base.BaseViewModel
import com.michaldrabik.showly2.ui.followedshows.myshows.helpers.MyShowsSearchResult
import com.michaldrabik.showly2.ui.followedshows.myshows.helpers.ResultType.EMPTY
import com.michaldrabik.showly2.ui.followedshows.myshows.helpers.ResultType.NO_RESULTS
import com.michaldrabik.showly2.ui.followedshows.myshows.helpers.ResultType.RESULTS
import com.michaldrabik.showly2.ui.followedshows.myshows.recycler.MyShowsListItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class FollowedShowsViewModel @Inject constructor(
  private val interactor: FollowedShowsInteractor
) : BaseViewModel() {

  val uiStream by lazy { MutableLiveData<FollowedShowsUiModel>() }
  private var searchJob: Job? = null

  var searchViewTranslation = 0F
  var tabsTranslation = 0F

  fun searchMyShows(query: String) {
    if (query.trim().isBlank()) {
      searchJob?.cancel()
      val result = MyShowsSearchResult(emptyList(), EMPTY)
      postSearchResult(result)
      return
    }
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
      val results = interactor.searchMyShows(query)
        .map {
          val image = interactor.findCachedImage(it, ImageType.FANART)
          MyShowsListItem(it, image)
        }
      val type = if (results.isEmpty()) NO_RESULTS else RESULTS
      val searchResult = MyShowsSearchResult(results, type)
      postSearchResult(searchResult)
    }
  }

  private fun postSearchResult(searchResult: MyShowsSearchResult) {
    uiStream.value = FollowedShowsUiModel(searchResult = searchResult)
    uiStream.value = FollowedShowsUiModel()
  }

  fun clearCache() = interactor.clearCache()
}
