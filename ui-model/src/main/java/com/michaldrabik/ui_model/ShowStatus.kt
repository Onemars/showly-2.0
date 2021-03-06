package com.michaldrabik.ui_model

enum class ShowStatus(
  val key: String,
  val displayName: String
) {
  RETURNING("returning series", "Returning Series"),
  UPCOMING("upcoming", "Upcoming"),
  IN_PRODUCTION("in production", "In Production"),
  PLANNED("planned", "Planned"),
  CANCELED("canceled", "Canceled"),
  ENDED("ended", "Ended"),
  UNKNOWN("unknown", "");

  fun isAnticipated() = this in arrayOf(UPCOMING, IN_PRODUCTION, PLANNED)

  companion object {
    fun fromKey(key: String?) =
      enumValues<ShowStatus>().firstOrNull { it.key == key } ?: UNKNOWN
  }
}
