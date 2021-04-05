package com.rudyrachman16.favoritegithubuser.data.api

import com.rudyrachman16.favoritegithubuser.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APICall {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder().apply {
            baseUrl(BuildConfig.BASE_URL)
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }
    val apiReq: APIRequest by lazy { retrofit.create(APIRequest::class.java) }
}