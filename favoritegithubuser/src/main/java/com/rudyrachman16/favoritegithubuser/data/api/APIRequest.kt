package com.rudyrachman16.favoritegithubuser.data.api

import com.rudyrachman16.favoritegithubuser.BuildConfig
import com.rudyrachman16.favoritegithubuser.data.api.models.DetailUser
import com.rudyrachman16.favoritegithubuser.data.api.models.SearchUser
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface APIRequest {
    @GET("users/{user}")
    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    suspend fun getDetail(
        @Path("user") user: String
    ): DetailUser

    @GET("users/{user}/following?per_page=100")
    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    suspend fun getFollowing(
        @Path("user") user: String
    ): ArrayList<SearchUser>

    @GET("users/{user}/followers?per_page=100")
    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    suspend fun getFollowers(
        @Path("user") user: String
    ): ArrayList<SearchUser>
}