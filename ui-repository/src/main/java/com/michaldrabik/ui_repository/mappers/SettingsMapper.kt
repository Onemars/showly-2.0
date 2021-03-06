package com.michaldrabik.ui_repository.mappers

import com.michaldrabik.ui_model.Genre
import com.michaldrabik.ui_model.NotificationDelay
import com.michaldrabik.ui_model.Settings
import javax.inject.Inject
import com.michaldrabik.storage.database.model.Settings as SettingsDb

class SettingsMapper @Inject constructor() {

  fun fromDatabase(settings: SettingsDb) = Settings(
    isInitialRun = settings.isInitialRun,
    pushNotificationsEnabled = settings.pushNotificationsEnabled,
    episodesNotificationsEnabled = settings.episodesNotificationsEnabled,
    episodesNotificationsDelay = NotificationDelay.fromDelay(settings.episodesNotificationsDelay),
    myShowsWatchingSortBy = enumValueOf(settings.myShowsRunningSortBy),
    myShowsUpcomingSortBy = enumValueOf(settings.myShowsIncomingSortBy),
    myShowsFinishedSortBy = enumValueOf(settings.myShowsEndedSortBy),
    myShowsAllSortBy = enumValueOf(settings.myShowsAllSortBy),
    myShowsRunningIsCollapsed = settings.myShowsRunningIsCollapsed,
    myShowsIncomingIsCollapsed = settings.myShowsIncomingIsCollapsed,
    myShowsEndedIsCollapsed = settings.myShowsEndedIsCollapsed,
    myShowsRunningIsEnabled = settings.myShowsRunningIsEnabled,
    myShowsIncomingIsEnabled = settings.myShowsIncomingIsEnabled,
    myShowsEndedIsEnabled = settings.myShowsEndedIsEnabled,
    myShowsRecentIsEnabled = settings.myShowsRecentIsEnabled,
    myShowsRecentsAmount = settings.myShowsRecentsAmount,
    seeLaterShowsSortBy = enumValueOf(settings.seeLaterShowsSortBy),
    archiveShowsSortBy = enumValueOf(settings.archiveShowsSortBy),
    showAnticipatedShows = settings.showAnticipatedShows,
    discoverFilterFeed = enumValueOf(settings.discoverFilterFeed),
    discoverFilterGenres = settings.discoverFilterGenres.split(",").filter { it.isNotBlank() }.map { Genre.valueOf(it) },
    traktSyncSchedule = enumValueOf(settings.traktSyncSchedule),
    traktQuickSyncEnabled = settings.traktQuickSyncEnabled,
    traktQuickRemoveEnabled = settings.traktQuickRemoveEnabled,
    watchlistSortOrder = enumValueOf(settings.watchlistSortBy),
    archiveShowsIncludeStatistics = settings.archiveShowsIncludeStatistics,
    specialSeasonsEnabled = settings.specialSeasonsEnabled
  )

  fun toDatabase(settings: Settings) = SettingsDb(
    isInitialRun = settings.isInitialRun,
    pushNotificationsEnabled = settings.pushNotificationsEnabled,
    episodesNotificationsEnabled = settings.episodesNotificationsEnabled,
    episodesNotificationsDelay = settings.episodesNotificationsDelay.delayMs,
    myShowsRunningSortBy = settings.myShowsWatchingSortBy.name,
    myShowsIncomingSortBy = settings.myShowsUpcomingSortBy.name,
    myShowsEndedSortBy = settings.myShowsFinishedSortBy.name,
    myShowsAllSortBy = settings.myShowsAllSortBy.name,
    myShowsRunningIsCollapsed = settings.myShowsRunningIsCollapsed,
    myShowsIncomingIsCollapsed = settings.myShowsIncomingIsCollapsed,
    myShowsEndedIsCollapsed = settings.myShowsEndedIsCollapsed,
    myShowsRunningIsEnabled = settings.myShowsRunningIsEnabled,
    myShowsIncomingIsEnabled = settings.myShowsIncomingIsEnabled,
    myShowsEndedIsEnabled = settings.myShowsEndedIsEnabled,
    myShowsRecentIsEnabled = settings.myShowsRecentIsEnabled,
    myShowsRecentsAmount = settings.myShowsRecentsAmount,
    seeLaterShowsSortBy = settings.seeLaterShowsSortBy.name,
    archiveShowsSortBy = settings.archiveShowsSortBy.name,
    showAnticipatedShows = settings.showAnticipatedShows,
    discoverFilterFeed = settings.discoverFilterFeed.name,
    discoverFilterGenres = settings.discoverFilterGenres.joinToString(",") { it.name },
    traktSyncSchedule = settings.traktSyncSchedule.name,
    traktQuickSyncEnabled = settings.traktQuickSyncEnabled,
    traktQuickRemoveEnabled = settings.traktQuickRemoveEnabled,
    watchlistSortBy = settings.watchlistSortOrder.name,
    archiveShowsIncludeStatistics = settings.archiveShowsIncludeStatistics,
    specialSeasonsEnabled = settings.specialSeasonsEnabled
  )
}
