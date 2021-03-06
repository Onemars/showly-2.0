package com.michaldrabik.ui_progress.progress.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.michaldrabik.ui_base.common.views.ShowView
import com.michaldrabik.ui_base.utilities.DurationPrinter
import com.michaldrabik.ui_base.utilities.extensions.addRipple
import com.michaldrabik.ui_base.utilities.extensions.bump
import com.michaldrabik.ui_base.utilities.extensions.colorFromAttr
import com.michaldrabik.ui_base.utilities.extensions.colorStateListFromAttr
import com.michaldrabik.ui_base.utilities.extensions.dimenToPx
import com.michaldrabik.ui_base.utilities.extensions.expandTouch
import com.michaldrabik.ui_base.utilities.extensions.gone
import com.michaldrabik.ui_base.utilities.extensions.onClick
import com.michaldrabik.ui_base.utilities.extensions.visible
import com.michaldrabik.ui_base.utilities.extensions.visibleIf
import com.michaldrabik.ui_progress.ProgressItem
import com.michaldrabik.ui_progress.R
import kotlinx.android.synthetic.main.view_progress_main_item.view.*
import kotlin.math.roundToInt

@SuppressLint("SetTextI18n")
class ProgressMainItemView : ShowView<ProgressItem> {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  var itemLongClickListener: ((ProgressItem, View) -> Unit)? = null
  var detailsClickListener: ((ProgressItem) -> Unit)? = null
  var checkClickListener: ((ProgressItem) -> Unit)? = null
  var missingImageListener: ((ProgressItem, Boolean) -> Unit)? = null

  init {
    inflate(context, R.layout.view_progress_main_item, this)
    layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    addRipple()
    progressItemCheckButton.expandTouch(100)

    onClick { itemClickListener?.invoke(item) }
    setOnLongClickListener {
      itemLongClickListener?.invoke(item, progressItemTitle)
      true
    }
    progressItemInfoButton.onClick { detailsClickListener?.invoke(item) }
  }

  private lateinit var item: ProgressItem

  override val imageView: ImageView = progressItemImage
  override val placeholderView: ImageView = progressItemPlaceholder

  private val durationPrinter by lazy { DurationPrinter(context.applicationContext) }
  private val checkButtonWidth by lazy { context.dimenToPx(R.dimen.progressItemCheckButtonWidth) }
  private val checkButtonHeight by lazy { context.dimenToPx(R.dimen.progressItemButtonHeight) }

  fun bind(item: ProgressItem) {
    this.item = item
    clear()

    progressItemTitle.text = item.show.title
    val episodeTitle = if (item.episode.title.isBlank()) "TBA" else item.episode.title
    progressItemSubtitle.text = String.format(
      "S.%02d E.%02d",
      item.episode.season,
      item.episode.number
    )
    progressItemSubtitle2.text = episodeTitle
    progressItemNewBadge.visibleIf(item.isNew())
    progressItemPin.visibleIf(item.isPinned)

    bindProgress(item)
    bindCheckButton(item, checkClickListener, detailsClickListener)

    loadImage(item, missingImageListener!!)
  }

  private fun bindProgress(item: ProgressItem) {
    val percent = ((item.watchedEpisodesCount.toFloat() / item.episodesCount.toFloat()) * 100).roundToInt()
    progressItemProgress.max = item.episodesCount
    progressItemProgress.progress = item.watchedEpisodesCount
    progressItemProgressText.text = "${item.watchedEpisodesCount}/${item.episodesCount} ($percent%)"
  }

  private fun bindCheckButton(
    item: ProgressItem,
    checkClickListener: ((ProgressItem) -> Unit)?,
    detailsClickListener: ((ProgressItem) -> Unit)?
  ) {
    val hasAired = item.episode.hasAired(item.season)
    val color = if (hasAired) android.R.attr.textColorPrimary else android.R.attr.textColorSecondary
    if (hasAired) {
      progressItemInfoButton.visible()
      progressItemCheckButton.run {
        layoutParams = LinearLayout.LayoutParams(checkButtonWidth, checkButtonHeight)
        text = ""
        setIconResource(R.drawable.ic_check)
        onClick { it.bump { checkClickListener?.invoke(item) } }
      }
    } else {
      progressItemInfoButton.gone()
      progressItemCheckButton.run {
        layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, checkButtonHeight)
        text = durationPrinter.print(item.episode.firstAired)
        icon = null
        onClick { detailsClickListener?.invoke(item) }
      }
    }
    progressItemCheckButton.setTextColor(context.colorFromAttr(color))
    progressItemCheckButton.strokeColor = context.colorStateListFromAttr(color)
    progressItemCheckButton.iconTint = context.colorStateListFromAttr(color)
  }

  private fun clear() {
    progressItemTitle.text = ""
    progressItemSubtitle.text = ""
    progressItemSubtitle2.text = ""
    progressItemProgressText.text = ""
    progressItemPlaceholder.gone()
    Glide.with(this).clear(progressItemImage)
  }
}
