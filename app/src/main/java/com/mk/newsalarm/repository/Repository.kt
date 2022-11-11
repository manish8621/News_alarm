package com.mk.newsalarm.repository

import com.mk.newsalarm.model.api.ApiClient
import com.mk.newsalarm.model.api.NetworkResponse
import com.mk.newsalarm.model.api.asDomainModels
import retrofit2.Response

class Repository() {
    suspend fun getNewsList(): NetworkResponse {
        return try{
            val res = ApiClient.newsApi.getNewsList().await()
            NetworkResponse.Success(res.data.asDomainModels())
        }
        catch (e:Exception)
        {
            NetworkResponse.Error(e.message.toString())
        }
    }
}