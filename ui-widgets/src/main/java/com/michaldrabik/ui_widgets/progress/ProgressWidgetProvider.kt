package com.michaldrabik.ui_widgets.progress

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.appwidget.AppWidgetManager.getInstance
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.URI_INTENT_SCHEME
import android.net.Uri
import android.widget.RemoteViews
import com.michaldrabik.common.Config.HOST_ACTIVITY_NAME
import com.michaldrabik.ui_model.IdTrakt
import com.michaldrabik.ui_widgets.R
import timber.log.Timber

class ProgressWidgetProvider : AppWidgetProvider() {

  companion object {
    const val ACTION_LIST_CLICK = "ACTION_LIST_CLICK"
    const val EXTRA_SHOW_ID = "EXTRA_SHOW_ID"
    const val EXTRA_SEASON_ID = "EXTRA_SEASON_ID"
    const val EXTRA_EPISODE_ID = "EXTRA_EPISODE_ID"

    fun requestUpdate(context: Context) {
      val applicationContext = context.applicationContext
      val intent = Intent(applicationContext, ProgressWidgetProvider::class.java).apply {
        val ids: IntArray = getInstance(applicationContext)
          .getAppWidgetIds(ComponentName(applicationContext, ProgressWidgetProvider::class.java))
        action = ACTION_APPWIDGET_UPDATE
        putExtra(EXTRA_APPWIDGET_IDS, ids)
      }
      applicationContext.sendBroadcast(intent)
      Timber.d("Widget update requested.")
    }
  }

  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray?
  ) {
    appWidgetIds?.forEach { updateWidget(context, appWidgetManager, it) }
    super.onUpdate(context, appWidgetManager, appWidgetIds)
  }

  private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
    val intent = Intent(context, ProgressWidgetService::class.java).apply {
      putExtra(EXTRA_APPWIDGET_ID, widgetId)
      data = Uri.parse(toUri(URI_INTENT_SCHEME))
    }

    val remoteViews = RemoteViews(context.packageName, R.layout.widget_progress).apply {
      setRemoteAdapter(R.id.progressWidgetList, intent)
      setEmptyView(R.id.progressWidgetList, R.id.progressWidgetEmptyView)
    }

    val listClickIntent = Intent(context, ProgressWidgetProvider::class.java).apply {
      action = ACTION_LIST_CLICK
      data = Uri.parse(intent.toUri(URI_INTENT_SCHEME))
    }
    val showDetailsPendingIntent = PendingIntent.getBroadcast(context, 0, listClickIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    remoteViews.setPendingIntentTemplate(R.id.progressWidgetList, showDetailsPendingIntent)

    appWidgetManager.updateAppWidget(widgetId, remoteViews)
    appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.progressWidgetList)
  }

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action.equals(ACTION_LIST_CLICK)) {
      when {
        intent.extras?.containsKey(EXTRA_EPISODE_ID) == true -> {
          val episodeId = intent.getLongExtra(EXTRA_EPISODE_ID, -1L)
          val seasonId = intent.getLongExtra(EXTRA_SEASON_ID, -1L)
          val showId = intent.getLongExtra(EXTRA_SHOW_ID, -1L)
          ProgressWidgetEpisodeCheckService.initialize(
            context.applicationContext,
            episodeId,
            seasonId,
            IdTrakt(showId)
          )
        }
        intent.extras?.containsKey(EXTRA_SHOW_ID) == true -> {
          val showId = intent.getLongExtra(EXTRA_SHOW_ID, -1L)
          context.startActivity(
            Intent().apply {
              setClassName(context, HOST_ACTIVITY_NAME)
              putExtra(EXTRA_SHOW_ID, showId.toString())
              flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
          )
        }
      }
    }
    super.onReceive(context, intent)
  }
}
