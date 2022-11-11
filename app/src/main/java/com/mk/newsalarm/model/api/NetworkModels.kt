package com.mk.newsalarm.model.api

import com.mk.newsalarm.model.domain.DomainModel
import com.squareup.moshi.JsonClass

class NetworkModels {

    @JsonClass(generateAdapter = true)
    data class NewsContainer(
        val data:List<News>
    )

    @JsonClass(generateAdapter = true)
    data class News(
        val author :String?,
        val content:String?,
        val date :String?,
        val id:String?,
        val imageUrl:String?,
        val time :String?,
        val title: String?,
        val url:String?
    )
}

fun NetworkModels.News.asDomainModel():DomainModel.News{
    return DomainModel.News(
        author=author?:"not found",
        content = content?:"not found",
        date = date?:"not found",
        id = id?:"not found",
        imageUrl = imageUrl?:"not found",
        time = time?:"not found",
        title = title?:"not found",
        url = url?:"not found",
        )
}
fun List<NetworkModels.News>.asDomainModels():List<DomainModel.News>{
    return map { it.asDomainModel() }
}