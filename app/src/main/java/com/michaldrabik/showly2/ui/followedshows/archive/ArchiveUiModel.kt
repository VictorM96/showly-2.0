package com.michaldrabik.showly2.ui.followedshows.archive

import com.michaldrabik.showly2.model.SortOrder
import com.michaldrabik.showly2.ui.common.UiModel
import com.michaldrabik.showly2.ui.followedshows.archive.recycler.ArchiveListItem

data class ArchiveUiModel(
  val items: List<ArchiveListItem>? = null,
  val sortOrder: SortOrder? = null
) : UiModel() {

  override fun update(newModel: UiModel) =
    (newModel as ArchiveUiModel).copy(
      items = newModel.items?.toList() ?: items,
      sortOrder = newModel.sortOrder ?: sortOrder
    )
}