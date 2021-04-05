package com.rudyrachman16.githubuserv2.background.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.rudyrachman16.githubuserv2.view.adapters.StackAdapter

class StackService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory =
        StackAdapter(applicationContext)
}