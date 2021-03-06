package com.michaldrabik.ui_progress.calendar.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.michaldrabik.common.extensions.toDisplayString
import com.michaldrabik.common.extensions.toLocalTimeZone
import com.michaldrabik.ui_base.common.views.ShowView
import com.michaldrabik.ui_base.utilities.extensions.addRipple
import com.michaldrabik.ui_base.utilities.extensions.expandTouch
import com.michaldrabik.ui_base.utilities.extensions.gone
import com.michaldrabik.ui_base.utilities.extensions.onClick
import com.michaldrabik.ui_progress.ProgressItem
import com.michaldrabik.ui_progress.R
import kotlinx.android.synthetic.main.view_progress_calendar_item.view.*

@SuppressLint("SetTextI18n")
class ProgressCalendarItemView : ShowView<ProgressItem> {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  var detailsClickListener: ((ProgressItem) -> Unit)? = null
  var missingImageListener: ((ProgressItem, Boolean) -> Unit)? = null

  init {
    inflate(context, R.layout.view_progress_calendar_item, this)
    layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    addRipple()

    onClick { itemClickListener?.invoke(item) }

    progressCalendarItemInfoButton.expandTouch(100)
    progressCalendarItemInfoButton.onClick { detailsClickListener?.invoke(item) }
  }

  private lateinit var item: ProgressItem

  override val imageView: ImageView = progressCalendarItemImage
  override val placeholderView: ImageView = progressCalendarItemPlaceholder

  fun bind(item: ProgressItem) {
    this.item = item
    clear()

    progressCalendarItemTitle.text = item.show.title
    progressCalendarItemDateText.text = item.upcomingEpisode.firstAired?.toLocalTimeZone()?.toDisplayString()

    val isNewSeason = item.upcomingEpisode.number == 1
    if (isNewSeason) {
      progressCalendarItemSubtitle2.text = context.getString(R.string.textSeason, item.upcomingEpisode.season)
      progressCalendarItemSubtitle.text = context.getString(R.string.textNewSeason)
    } else {
      val subtitle = if (item.upcomingEpisode.title.isBlank()) "TBA" else item.upcomingEpisode.title
      progressCalendarItemSubtitle2.text = subtitle
      progressCalendarItemSubtitle.text = String.format(
        "S.%02d E.%02d",
        item.upcomingEpisode.season,
        item.upcomingEpisode.number
      )
    }

    loadImage(item, missingImageListener!!)
  }

  private fun clear() {
    progressCalendarItemTitle.text = ""
    progressCalendarItemSubtitle.text = ""
    progressCalendarItemSubtitle2.text = ""
    progressCalendarItemDateText.text = ""
    progressCalendarItemPlaceholder.gone()
    Glide.with(this).clear(progressCalendarItemImage)
  }
}
