package com.michaldrabik.ui_my_shows.myshows.recycler

import com.michaldrabik.ui_base.common.ListItem
import com.michaldrabik.ui_model.Image
import com.michaldrabik.ui_model.ImageType.POSTER
import com.michaldrabik.ui_model.MyShowsSection
import com.michaldrabik.ui_model.Show
import com.michaldrabik.ui_model.SortOrder
import com.michaldrabik.ui_model.TraktRating

data class MyShowsItem(
  val type: Type,
  val header: Header?,
  val recentsSection: RecentsSection?,
  val horizontalSection: HorizontalSection?,
  override val show: Show,
  override val image: Image,
  override val isLoading: Boolean,
  val rating: TraktRating?
) : ListItem {

  enum class Type {
    HEADER,
    RECENT_SHOWS,
    HORIZONTAL_SHOWS,
    ALL_SHOWS_ITEM,
    SEARCH_SHOWS_ITEM
  }

  data class Header(
    val section: MyShowsSection,
    val itemCount: Int,
    val sortOrder: SortOrder?
  )

  data class RecentsSection(
    val items: List<MyShowsItem>
  )

  data class HorizontalSection(
    val section: MyShowsSection,
    val items: List<MyShowsItem>
  )

  companion object {
    fun createHeader(
      section: MyShowsSection,
      itemCount: Int,
      sortOrder: SortOrder?
    ) = MyShowsItem(
      Type.HEADER,
      Header(section, itemCount, sortOrder),
      null,
      null,
      Show.EMPTY,
      Image.createUnavailable(POSTER),
      false,
      null
    )

    fun createRecentsSection(
      shows: List<MyShowsItem>
    ) = MyShowsItem(
      Type.RECENT_SHOWS,
      null,
      RecentsSection(shows),
      null,
      Show.EMPTY,
      Image.createUnavailable(POSTER),
      false,
      null
    )

    fun createHorizontalSection(
      section: MyShowsSection,
      shows: List<MyShowsItem>
    ) = MyShowsItem(
      Type.HORIZONTAL_SHOWS,
      null,
      null,
      HorizontalSection(section, shows.map { it.copy(type = Type.HORIZONTAL_SHOWS) }),
      Show.EMPTY,
      Image.createUnavailable(POSTER),
      false,
      null
    )

    fun createSearchItem(
      show: Show,
      image: Image
    ) = MyShowsItem(
      Type.SEARCH_SHOWS_ITEM,
      null,
      null,
      null,
      show,
      image,
      false,
      null
    )
  }
}
