package com.michaldrabik.showly2.ui.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.michaldrabik.showly2.ui.common.behaviour.ScrollableViewBehaviour

class ScrollableImageView : AppCompatImageView, CoordinatorLayout.AttachedBehavior {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  override fun getBehavior() = ScrollableViewBehaviour()
}