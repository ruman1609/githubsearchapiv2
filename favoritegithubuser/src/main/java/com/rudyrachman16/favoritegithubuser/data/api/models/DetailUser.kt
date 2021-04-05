package com.rudyrachman16.favoritegithubuser.data.api.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailUser(
    @SerializedName("public_repos") val repo: Int,
    val followers: Int,
    val following: Int,
    @SerializedName("html_url") val url: String,
    val name: String?,
    val company: String?,
    val location: String?
) : Parcelable