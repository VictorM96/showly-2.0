package com.michaldrabik.ui_movie.cases

import com.michaldrabik.common.di.AppScope
import com.michaldrabik.ui_model.IdTrakt
import com.michaldrabik.ui_model.Movie
import com.michaldrabik.ui_repository.ListsRepository
import javax.inject.Inject

@AppScope
class MovieDetailsListsCase @Inject constructor(
  private val listsRepository: ListsRepository
) {

  suspend fun countLists(movie: Movie) =
    listsRepository.loadListIdsForItem(IdTrakt(movie.traktId), "movie").size
}