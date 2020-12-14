package com.michaldrabik.ui_my_movies.mymovies.cases

import com.michaldrabik.common.di.AppScope
import com.michaldrabik.ui_my_movies.mymovies.recycler.MyMoviesItem
import com.michaldrabik.ui_repository.RatingsRepository
import com.michaldrabik.ui_repository.UserTraktManager
import javax.inject.Inject

@AppScope
class MyMoviesRatingsCase @Inject constructor(
  private val ratingsRepository: RatingsRepository,
  private val userTraktManager: UserTraktManager
) {

  suspend fun loadRatings(items: MutableList<MyMoviesItem>): List<MyMoviesItem> {
    if (!userTraktManager.isAuthorized()) return items

    val token = userTraktManager.checkAuthorization().token
    ratingsRepository.preloadMoviesRatings(token)

    return items.map {
      val rating = ratingsRepository.loadRating(token, it.movie)
      it.copy(userRating = rating?.rating)
    }
  }
}