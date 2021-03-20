package com.michaldrabik.ui_show.cases

import com.michaldrabik.common.di.AppScope
import com.michaldrabik.ui_model.IdTrakt
import com.michaldrabik.ui_model.Show
import com.michaldrabik.ui_repository.ListsRepository
import javax.inject.Inject

@AppScope
class ShowDetailsListsCase @Inject constructor(
  private val listsRepository: ListsRepository
) {

  suspend fun countLists(show: Show) =
    listsRepository.loadListIdsForItem(IdTrakt(show.traktId), "show").size
}