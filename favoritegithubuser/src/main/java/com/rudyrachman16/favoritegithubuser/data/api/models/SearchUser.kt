package com.rudyrachman16.favoritegithubuser.data.api.models

import com.google.gson.annotations.SerializedName

data class SearchUser(
    val id: Int,
    @SerializedName("login") val username: String,
    @SerializedName("avatar_url") val picUrl: String,
    @Transient var showMenu: Boolean = false
)