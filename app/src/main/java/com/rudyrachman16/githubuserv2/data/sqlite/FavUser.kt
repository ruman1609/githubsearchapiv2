package com.rudyrachman16.githubuserv2.data.sqlite

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav_user")
data class FavUser(
    @PrimaryKey var id: Int,
    var username: String,
    var picUrl: String
) {
    companion object {
        const val ID = "id"
        const val USERNAME = "username"
        const val PIC_URL = "picUrl"

        const val AUTHORITY = "com.rudyrachman16.githubuserv2"
        private const val SCHEME = "content"
        const val TABLE_NAME = "fav_user"
        val CONTENT_URI: Uri = Uri.Builder().apply {
            scheme(SCHEME)
            authority(AUTHORITY)
            appendPath(TABLE_NAME)
        }.build()
    }
}
