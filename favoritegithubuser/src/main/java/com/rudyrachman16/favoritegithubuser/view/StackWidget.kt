package com.rudyrachman16.favoritegithubuser.view

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.rudyrachman16.favoritegithubuser.R
import com.rudyrachman16.favoritegithubuser.background.service.StackService
import com.rudyrachman16.favoritegithubuser.data.api.models.User
import com.rudyrachman16.favoritegithubuser.data.sqlite.FavUser
import com.rudyrachman16.favoritegithubuser.view.activities.DetailActivity
import com.rudyrachman16.favoritegithubuser.view.activities.FavoriteActivity

/**
 * Implementation of App Widget functionality.
 */
class StackWidget : AppWidgetProvider() {
    companion object {
        const val SEND = "send"
        private const val REFRESH = "fresh"
        fun updateWidget(context: Context) {
            PendingIntent.getBroadcast(
                context, -2, Intent(context, StackWidget::class.java).apply {
                    action = "fresh"
                }, PendingIntent.FLAG_UPDATE_CURRENT
            ).send()
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun makeViews(context: Context): RemoteViews {
        val intent = Intent(context, StackService::class.java).apply {
            data = toUri(Intent.URI_INTENT_SCHEME).toUri()
        }
        val views = RemoteViews(context.packageName, R.layout.stack_widget).apply {
            setRemoteAdapter(R.id.stackView, intent)
            setEmptyView(R.id.stackView, R.id.statusText)
        }
        val sendIntent = Intent(context, StackWidget::class.java).apply {
            action = SEND
            data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, -1, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setPendingIntentTemplate(R.id.stackView, pendingIntent)
        val click = PendingIntent.getBroadcast(
            context, -2, Intent(context, StackWidget::class.java).apply {
                action = "fresh"
            }, PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.titleText, click)
        return views
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = makeViews(context)
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stackView)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == SEND) {
            val user = User(
                intent.getIntExtra(FavUser.ID, -1),
                intent.getStringExtra(FavUser.USERNAME)!!,
                intent.getStringExtra(FavUser.PIC_URL)!!
            )
            val detail = Intent(context, DetailActivity::class.java).apply {
                putExtra(FavoriteActivity.USER_KEY, user)
                putExtra(SEND, true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(detail)
        } else if (intent?.action == REFRESH) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, StackWidget::class.java))
            manager.notifyAppWidgetViewDataChanged(ids, R.id.stackView)
        }
    }
}