package com.michaldrabik.ui_my_shows.myshows.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.michaldrabik.ui_base.common.views.ShowView
import com.michaldrabik.ui_base.utilities.extensions.gone
import com.michaldrabik.ui_base.utilities.extensions.onClick
import com.michaldrabik.ui_base.utilities.extensions.visibleIf
import com.michaldrabik.ui_my_shows.R
import com.michaldrabik.ui_my_shows.myshows.recycler.MyShowsItem
import kotlinx.android.synthetic.main.view_my_show_all.view.*

@SuppressLint("SetTextI18n")
class MyShowAllView : ShowView<MyShowsItem> {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  init {
    inflate(context, R.layout.view_my_show_all, this)
    layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    myShowAllRoot.onClick { itemClickListener?.invoke(item) }
  }

  override val imageView: ImageView = myShowAllImage
  override val placeholderView: ImageView = myShowAllPlaceholder

  private lateinit var item: MyShowsItem

  override fun bind(
    item: MyShowsItem,
    missingImageListener: (MyShowsItem, Boolean) -> Unit
  ) {
    clear()
    this.item = item
    myShowAllProgress.visibleIf(item.isLoading)
    myShowAllTitle.text = item.show.title
    myShowAllDescription.text = item.show.overview
    val year = if (item.show.year > 0) " (${item.show.year})" else ""
    myShowAllNetwork.text = "${item.show.network}$year"

    if (item.rating == null) {
      myShowAllRating.text = String.format("%.1f", item.show.rating)
    } else {
      myShowAllRating.text = String.format("%.1f | You: %d", item.show.rating, item.rating.rating)
    }

    myShowAllDescription.visibleIf(item.show.overview.isNotBlank())
    myShowAllNetwork.visibleIf(item.show.network.isNotBlank())

    loadImage(item, missingImageListener)
  }

  private fun clear() {
    myShowAllTitle.text = ""
    myShowAllDescription.text = ""
    myShowAllNetwork.text = ""
    myShowAllRating.text = ""
    myShowAllPlaceholder.gone()
    Glide.with(this).clear(myShowAllImage)
  }
}
