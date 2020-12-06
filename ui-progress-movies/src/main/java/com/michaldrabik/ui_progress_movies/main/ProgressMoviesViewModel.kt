package com.michaldrabik.ui_progress_movies.main

import androidx.lifecycle.viewModelScope
import com.michaldrabik.ui_base.BaseViewModel
import com.michaldrabik.ui_base.images.MovieImagesProvider
import com.michaldrabik.ui_model.ImageType.POSTER
import com.michaldrabik.ui_model.SortOrder
import com.michaldrabik.ui_model.SortOrder.RECENTLY_WATCHED
import com.michaldrabik.ui_progress_movies.ProgressMovieItem
import com.michaldrabik.ui_progress_movies.main.cases.ProgressMoviesLoadItemsCase
import com.michaldrabik.ui_progress_movies.main.cases.ProgressPinnedItemsCase
import com.michaldrabik.ui_progress_movies.main.cases.ProgressSortOrderCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProgressMoviesViewModel @Inject constructor(
  private val loadItemsCase: ProgressMoviesLoadItemsCase,
  private val pinnedItemsCase: ProgressPinnedItemsCase,
  private val sortOrderCase: ProgressSortOrderCase,
  private val imagesProvider: MovieImagesProvider
) : BaseViewModel<ProgressMoviesUiModel>() {

  private var searchQuery = ""

  fun loadProgress(resetScroll: Boolean = false) {
    viewModelScope.launch {
      val movies = loadItemsCase.loadMyMovies()
      val items = movies.map { movie ->
        async {
          val item = loadItemsCase.loadProgressItem(movie)
          val image = imagesProvider.findCachedImage(movie, POSTER)
          item.copy(image = image)
        }
      }.awaitAll()

      val sortOrder = sortOrderCase.loadSortOrder()
      val allItems = loadItemsCase.prepareItems(items, searchQuery, sortOrder)
      uiState =
        ProgressMoviesUiModel(
          items = allItems,
          isSearching = searchQuery.isNotBlank(),
          sortOrder = sortOrder,
          resetScroll = resetScroll && sortOrder == RECENTLY_WATCHED
        )
    }
  }

  fun searchQuery(searchQuery: String) {
    this.searchQuery = searchQuery.trim()
    loadProgress()
  }

  fun setSortOrder(sortOrder: SortOrder) {
    viewModelScope.launch {
      sortOrderCase.setSortOrder(sortOrder)
      loadProgress(resetScroll = true)
    }
  }

  fun togglePinItem(item: ProgressMovieItem) {
    if (item.isPinned) {
      pinnedItemsCase.removePinnedItem(item)
    } else {
      pinnedItemsCase.addPinnedItem(item)
    }
    loadProgress()
  }

  fun onOpenMovieDetails() {
    uiState = ProgressMoviesUiModel(resetScroll = false)
  }
}