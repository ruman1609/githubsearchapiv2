package com.rudyrachman16.githubuserv2.data.api

import com.rudyrachman16.githubuserv2.BuildConfig
import com.rudyrachman16.githubuserv2.data.api.models.DetailUser
import com.rudyrachman16.githubuserv2.data.api.models.SearchUser
import com.rudyrachman16.githubuserv2.data.api.models.Searches
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface APIRequest {

    @GET("search/users")
    @Headers("Authorization: token ${BuildConfig.API_KEY}")
    suspend fun getSearch(
        @Query("q") search: String,
        @Query("per_page") perPage: Int = 100
    ): Searches

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