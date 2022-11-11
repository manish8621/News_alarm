package com.mk.newsalarm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mk.newsalarm.model.api.NetworkResponse
import com.mk.newsalarm.model.api.States
import com.mk.newsalarm.model.domain.DomainModel
import com.mk.newsalarm.repository.Repository
import kotlinx.coroutines.*

class NewsListViewModel(application: Application) : AndroidViewModel(application) {
    val TAG="NewsListViewModel"


    val newsList = MutableLiveData<List<DomainModel.News>>()
    private val repository= Repository()

    val status = MutableLiveData<States>(States.LOADING)

    fun loadNews() {
        viewModelScope.launch()
        {
            val networkResponse = repository.getNewsList()
            when(networkResponse){
                is NetworkResponse.Success -> {
                    newsList.postValue(networkResponse.response)
                    status.postValue(States.SUCCESS)
                }
                is NetworkResponse.Error -> {
                    status.postValue(States.ERROR)
                }
            }

        }
    }



}