package com.michaldrabik.showly2.model

data class Settings(
  val isInitialRun: Boolean,
  val pushNotificationsEnabled: Boolean,
  val showsNotificationsEnabled: Boolean,
  val myShowsRunningSortBy: SortOrder,
  val myShowsIncomingSortBy: SortOrder,
  val myShowsEndedSortBy: SortOrder,
  val myShowsRecentsAmount: Int
)