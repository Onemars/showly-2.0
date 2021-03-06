package com.michaldrabik.ui_show.episodes

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintLayout
import com.michaldrabik.common.extensions.toDisplayString
import com.michaldrabik.common.extensions.toLocalTimeZone
import com.michaldrabik.ui_base.utilities.extensions.addRipple
import com.michaldrabik.ui_base.utilities.extensions.expandTouch
import com.michaldrabik.ui_base.utilities.extensions.onClick
import com.michaldrabik.ui_model.Episode
import com.michaldrabik.ui_show.R
import kotlinx.android.synthetic.main.view_episode.view.*

class EpisodeView : ConstraintLayout {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  init {
    inflate(context, R.layout.view_episode, this)
    layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    addRipple()
    post { episodeCheckbox.expandTouch() }
  }

  fun bind(
    item: EpisodeListItem,
    itemClickListener: (Episode, Boolean) -> Unit,
    itemCheckedListener: (Episode, Boolean) -> Unit
  ) {
    clear()

    val hasAired = item.episode.hasAired(item.season)
    episodeTitle.text = context.getString(R.string.textEpisode, item.episode.number)
    episodeOverview.text = item.episode.title.ifEmpty { "TBA" }
    episodeCheckbox.isChecked = item.isWatched
    episodeCheckbox.isEnabled = hasAired

    if (hasAired) {
      episodeCheckbox.setOnCheckedChangeListener { _, isChecked ->
        itemCheckedListener(item.episode, isChecked)
      }
    } else {
      val date = item.episode.firstAired?.toLocalTimeZone()
      episodeTitle.text = context.getString(R.string.textEpisodeDate, item.episode.number, date?.toDisplayString() ?: "TBA")
    }

    onClick { itemClickListener(item.episode, item.isWatched) }
  }

  private fun clear() {
    episodeCheckbox.setOnCheckedChangeListener { _, _ -> }
  }
}
