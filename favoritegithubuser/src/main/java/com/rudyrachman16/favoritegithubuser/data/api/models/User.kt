package com.rudyrachman16.favoritegithubuser.data.api.models

import android.os.Parcelable
import com.rudyrachman16.favoritegithubuser.data.api.APICall
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val username: String,
    val picUrl: String
) : Parcelable {
    suspend fun getDetail(username: String = this.username): DetailUser =
        APICall.apiReq.getDetail(username)

    suspend fun getFollowers(username: String = this.username): ArrayList<SearchUser> =
        APICall.apiReq.getFollowers(username)

    suspend fun getFollowing(username: String = this.username): ArrayList<SearchUser> =
        APICall.apiReq.getFollowing(username)
}