package com.rudyrachman16.githubuserv2.view.adapters

import android.content.Context
import android.content.Intent
import android.os.Binder
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.rudyrachman16.githubuserv2.R
import com.rudyrachman16.githubuserv2.data.api.models.SearchUser
import com.rudyrachman16.githubuserv2.data.api.models.User
import com.rudyrachman16.githubuserv2.data.sqlite.FavUser
import com.rudyrachman16.githubuserv2.view.activities.FavoriteActivity

class StackAdapter(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private val list = ArrayList<SearchUser>()
    override fun onCreate() {}

    override fun onDataSetChanged() {
        println("WAW")
        list.clear()
        val identity = Binder.clearCallingIdentity()  // synchronized content need this
        val cursor = context.contentResolver.query(
            FavUser.CONTENT_URI, null, null,
            null, null, null
        )!!
        list.addAll(FavoriteActivity.cursorMapping(cursor))
        Binder.restoreCallingIdentity(identity)  // synchronized content need this
        cursor.close()
    }

    override fun onDestroy() {}

    override fun getCount(): Int = list.size

    override fun getViewAt(position: Int): RemoteViews? {
        val user = User(list[position].id, list[position].username, list[position].picUrl)
        val intent = Intent().apply {
            putExtra(FavUser.ID, user.id)
            putExtra(FavUser.USERNAME, user.username)
            putExtra(FavUser.PIC_URL, user.picUrl)
        }
        return try {
            val bitmap =
                Glide.with(context).asBitmap().load(list[position].picUrl).submit(100, 100).get()
            RemoteViews(context.packageName, R.layout.widget_item).apply {
                setImageViewBitmap(R.id.imageView, bitmap)
                setTextViewText(R.id.username, list[position].username)
                setTextViewText(R.id.id, list[position].id.toString())
                setOnClickFillInIntent(R.id.bodyLayout, intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}