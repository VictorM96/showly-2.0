package com.michaldrabik.ui_base.utilities

class ActionEvent<T>(
  private val action: T,
) {

  private var isConsumed: Boolean = false

  fun peek(): T = action

  fun consume(): T? =
    if (!isConsumed) {
      isConsumed = true
      action
    } else {
      null
    }
}
