package com.michaldrabik.ui_trakt_sync

import com.michaldrabik.ui_base.UiModel
import com.michaldrabik.ui_model.TraktSyncSchedule

data class TraktSyncUiModel(
  val isProgress: Boolean? = null,
  val progressStatus: String? = null,
  val isAuthorized: Boolean? = null,
  val authError: Boolean? = null,
  val traktSyncSchedule: TraktSyncSchedule? = null,
  val quickSyncEnabled: Boolean? = null,
  val lastTraktSyncTimestamp: Long? = null
) : UiModel() {

  override fun update(newModel: UiModel) =
    (newModel as TraktSyncUiModel).copy(
      isProgress = newModel.isProgress ?: isProgress,
      progressStatus = newModel.progressStatus ?: progressStatus,
      isAuthorized = newModel.isAuthorized ?: isAuthorized,
      authError = newModel.authError ?: authError,
      traktSyncSchedule = newModel.traktSyncSchedule ?: traktSyncSchedule,
      quickSyncEnabled = newModel.quickSyncEnabled ?: quickSyncEnabled,
      lastTraktSyncTimestamp = newModel.lastTraktSyncTimestamp ?: lastTraktSyncTimestamp
    )
}
