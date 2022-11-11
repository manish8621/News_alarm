package com.mk.newsalarm.model.api

import com.mk.newsalarm.model.domain.DomainModel

sealed class NetworkResponse() {
    class Success(val response:List<DomainModel.News>):NetworkResponse()
    class Error(val errorMessage:String):NetworkResponse()
}