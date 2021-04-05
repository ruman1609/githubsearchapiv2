package com.rudyrachman16.githubuserv2.data

import android.content.Context
import com.rudyrachman16.githubuserv2.data.api.APICall
import com.rudyrachman16.githubuserv2.data.sqlite.FavDatabase
import com.rudyrachman16.githubuserv2.data.sqlite.FavUser

class DataRepos(context: Context) {
    suspend fun getSearch(search: String) = APICall.apiReq.getSearch(search)  // API - Search

    // Room Database
    private val favDB = FavDatabase.getInstance(context)
    private var favDao = favDB.favDao()

    fun getFavList() = favDao.getAll()

    fun insertFav(favUser: FavUser) = favDao.insert(favUser)

    fun deleteFav(id: String) = favDao.delete(id)

    fun isFav(id: String) = favDao.available(id)
}