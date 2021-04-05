package com.rudyrachman16.favoritegithubuser.background.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.rudyrachman16.favoritegithubuser.view.adapters.StackAdapter

class StackService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory =
        StackAdapter(applicationContext)
}