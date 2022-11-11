package com.mk.newsalarm.model.domain

import android.icu.text.CaseMap.Title

class DomainModel {
    data class News(
        val author :String,
        val title: String,
        val content:String,
        val date :String,
        val id:String,
        val imageUrl:String,
        val time :String,
        val url:String
    )
}