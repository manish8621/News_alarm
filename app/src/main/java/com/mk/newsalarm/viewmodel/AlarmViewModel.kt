package com.mk.newsalarm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mk.newsalarm.model.api.NetworkResponse
import com.mk.newsalarm.model.api.States
import com.mk.newsalarm.model.domain.DomainModel
import com.mk.newsalarm.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmViewModel:ViewModel() {
    val repository = Repository()
    val responseState = MutableLiveData<NetworkResponse>()


    fun loadNewsList(){
        viewModelScope.launch(Dispatchers.IO) {
            val networkResponse = repository.getNewsList()
            responseState.postValue(networkResponse)

        }
    }
}