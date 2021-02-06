package com.michaldrabik.ui_episodes.details

import androidx.lifecycle.viewModelScope
import com.michaldrabik.common.Config
import com.michaldrabik.network.Cloud
import com.michaldrabik.ui_base.Analytics
import com.michaldrabik.ui_base.BaseViewModel
import com.michaldrabik.ui_base.dates.DateFormatProvider
import com.michaldrabik.ui_base.images.EpisodeImagesProvider
import com.michaldrabik.ui_base.utilities.ActionEvent
import com.michaldrabik.ui_base.utilities.MessageEvent
import com.michaldrabik.ui_episodes.R
import com.michaldrabik.ui_model.Episode
import com.michaldrabik.ui_model.IdTmdb
import com.michaldrabik.ui_model.IdTrakt
import com.michaldrabik.ui_model.RatingState
import com.michaldrabik.ui_model.TraktRating
import com.michaldrabik.ui_repository.RatingsRepository
import com.michaldrabik.ui_repository.TranslationsRepository
import com.michaldrabik.ui_repository.UserTraktManager
import com.michaldrabik.ui_repository.mappers.Mappers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class EpisodeDetailsViewModel @Inject constructor(
  private val imagesProvider: EpisodeImagesProvider,
  private val dateFormatProvider: DateFormatProvider,
  private val ratingsRepository: RatingsRepository,
  private val translationsRepository: TranslationsRepository,
  private val userTraktManager: UserTraktManager,
  private val mappers: Mappers,
  private val cloud: Cloud
) : BaseViewModel<EpisodeDetailsUiModel>() {

  init {
    uiState = EpisodeDetailsUiModel(dateFormat = dateFormatProvider.loadFullHourFormat())
  }

  fun loadImage(showId: IdTmdb, episode: Episode) {
    viewModelScope.launch {
      try {
        uiState = EpisodeDetailsUiModel(imageLoading = true)
        val episodeImage = imagesProvider.loadRemoteImage(showId, episode)
        uiState = EpisodeDetailsUiModel(image = episodeImage, imageLoading = false)
      } catch (t: Throwable) {
        uiState = EpisodeDetailsUiModel(imageLoading = false)
      }
    }
  }

  fun loadTranslation(showTraktId: IdTrakt, episode: Episode) {
    viewModelScope.launch {
      try {
        val language = translationsRepository.getLanguage()
        if (language == Config.DEFAULT_LANGUAGE) return@launch
        val translation = translationsRepository.loadTranslation(episode, showTraktId, language)
        translation?.let {
          uiState = EpisodeDetailsUiModel(translation = ActionEvent(it))
        }
      } catch (error: Throwable) {
        Timber.e(error)
      }
    }
  }

  fun loadComments(idTrakt: IdTrakt, season: Int, episode: Int) {
    viewModelScope.launch {
      try {
        uiState = EpisodeDetailsUiModel(commentsLoading = true)

        val comments = cloud.traktApi.fetchEpisodeComments(idTrakt.id, season, episode)
          .map { mappers.comment.fromNetwork(it) }
          .filter { it.parentId <= 0 }
          .sortedByDescending { it.id }

        uiState = EpisodeDetailsUiModel(
          comments = comments,
          commentsLoading = false,
          commentsDateFormat = dateFormatProvider.loadShortDayFormat()
        )
      } catch (t: Throwable) {
        Timber.w("Failed to load comments. ${t.message}")
        uiState = EpisodeDetailsUiModel(commentsLoading = false)
      }
    }
  }

  fun loadRatings(episode: Episode) {
    viewModelScope.launch {
      try {
        if (!userTraktManager.isAuthorized()) {
          uiState = EpisodeDetailsUiModel(ratingState = RatingState(rateAllowed = false, rateLoading = false))
          return@launch
        }
        uiState = EpisodeDetailsUiModel(ratingState = RatingState(rateAllowed = true, rateLoading = true))
        val token = userTraktManager.checkAuthorization()
        val rating = ratingsRepository.shows.loadRating(token.token, episode)
        uiState = EpisodeDetailsUiModel(ratingState = RatingState(userRating = rating, rateLoading = false))
      } catch (error: Throwable) {
        uiState = EpisodeDetailsUiModel(ratingState = RatingState(rateLoading = false))
      }
    }
  }

  fun addRating(rating: Int, episode: Episode, showTraktId: IdTrakt) {
    viewModelScope.launch {
      try {
        val token = userTraktManager.checkAuthorization().token
        uiState = EpisodeDetailsUiModel(ratingState = RatingState(rateLoading = true))
        ratingsRepository.shows.addRating(token, episode, rating)
        _messageLiveData.value = MessageEvent.info(R.string.textShowRated)
        uiState = EpisodeDetailsUiModel(
          ratingState = RatingState(userRating = TraktRating(episode.ids.trakt, rating)),
          ratingChanged = ActionEvent(true)
        )
        Analytics.logEpisodeRated(showTraktId.id, episode, rating)
      } catch (error: Throwable) {
        _messageLiveData.value = MessageEvent.error(R.string.errorGeneral)
      } finally {
        uiState = EpisodeDetailsUiModel(ratingState = RatingState(rateLoading = false))
      }
    }
  }

  fun deleteRating(episode: Episode) {
    viewModelScope.launch {
      try {
        uiState = EpisodeDetailsUiModel(ratingState = RatingState(rateLoading = true))
        val token = userTraktManager.checkAuthorization().token
        ratingsRepository.shows.deleteRating(token, episode)
        uiState = EpisodeDetailsUiModel(
          ratingState = RatingState(userRating = TraktRating.EMPTY, rateLoading = false),
          ratingChanged = ActionEvent(true)

        )
        _messageLiveData.value = MessageEvent.info(R.string.textShowRatingDeleted)
      } catch (error: Throwable) {
        uiState = EpisodeDetailsUiModel(ratingState = RatingState(rateLoading = false))
        _messageLiveData.value = MessageEvent.error(R.string.errorGeneral)
      }
    }
  }
}
