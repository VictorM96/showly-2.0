package com.michaldrabik.ui_progress.calendar.helpers.filters

import com.michaldrabik.common.extensions.toLocalZone
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit.DAYS
import javax.inject.Inject
import javax.inject.Singleton
import com.michaldrabik.data_local.database.model.Episode as EpisodeDb

@Singleton
class CalendarFutureFilter @Inject constructor() : CalendarFilter {

  override fun filter(now: ZonedDateTime, episode: EpisodeDb): Boolean {
    val dateDays = episode.firstAired?.toLocalZone()?.truncatedTo(DAYS)
    val isTodayOrFuture = dateDays?.isEqual(now.truncatedTo(DAYS)) == true || dateDays?.isAfter(now.truncatedTo(DAYS)) == true
    return episode.seasonNumber != 0 && isTodayOrFuture
  }
}
