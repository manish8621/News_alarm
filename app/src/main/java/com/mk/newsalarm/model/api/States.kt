package com.mk.newsalarm.model.api

enum class States(val state:String) {
    LOADING("loading"),
    ERROR("ERROR"),
    NOT_LOADING("not loading"),
    SUCCESS("success")
}