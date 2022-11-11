package com.mk.newsalarm.model.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApiService {
    @GET("news")
    fun getNewsList(@Query("category") category:String="all"):Deferred<NetworkModels.NewsContainer>
}

object ApiClient {
    private const val BASE_URL = "https://inshorts.deta.dev/"

    private val moshi = Moshi
        .Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    private val retroFit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .build()

    val newsApi = retroFit.create(NewsApiService::class.java)
}