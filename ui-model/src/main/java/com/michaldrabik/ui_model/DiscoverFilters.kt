package com.michaldrabik.ui_model

data class DiscoverFilters(
  val feedOrder: DiscoverSortOrder = DiscoverSortOrder.HOT,
  val hideAnticipated: Boolean = false,
  val genres: List<Genre> = emptyList()
) {

  fun isDefault() = feedOrder == DiscoverSortOrder.HOT && !hideAnticipated && genres.isEmpty()
}
