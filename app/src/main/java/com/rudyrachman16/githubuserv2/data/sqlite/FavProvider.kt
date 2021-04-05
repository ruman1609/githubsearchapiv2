package com.rudyrachman16.githubuserv2.data.sqlite

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.core.net.toUri
import com.rudyrachman16.githubuserv2.data.DataRepos

class FavProvider : ContentProvider() {
    companion object {
        private const val FAV = 1
        private const val FAV_ID = 2
        private val URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH)
    }

    init {
        URI_MATCHER.addURI(FavUser.AUTHORITY, FavUser.TABLE_NAME, FAV)
        URI_MATCHER.addURI(FavUser.AUTHORITY, "${FavUser.TABLE_NAME}/#", FAV_ID)
    }

    private lateinit var favRepo: DataRepos
    override fun onCreate(): Boolean {
        favRepo = DataRepos(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = when (URI_MATCHER.match(uri)) {
        FAV -> favRepo.getFavList()
        FAV_ID -> favRepo.isFav(uri.lastPathSegment.toString())
        else -> null
    }

    override fun getType(uri: Uri): String = uri.toString()

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val add = if (URI_MATCHER.match(uri) == FAV) favRepo.insertFav(
            FavUser(
                values?.getAsInteger(FavUser.ID)!!,
                values.getAsString(FavUser.USERNAME),
                values.getAsString(FavUser.PIC_URL)
            )
        ) else -1
        context?.contentResolver?.notifyChange(FavUser.CONTENT_URI, null)
        return "$FavUser.CONTENT_URI/$add".toUri()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        if (URI_MATCHER.match(uri) == FAV_ID) {
            val id = favRepo.deleteFav(uri.lastPathSegment.toString())
            context?.contentResolver?.notifyChange(FavUser.CONTENT_URI, null)
            return id
        }
        return -1
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = -1

}