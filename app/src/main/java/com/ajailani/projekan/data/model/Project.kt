package com.ajailani.projekan.data.model

import com.squareup.moshi.Json

data class Project(
    @Json(name = "id")
    var id: Int = 0,
    @Json(name = "icon")
    var icon: String = "",
    @Json(name = "title")
    var title: String = "",
    @Json(name = "desc")
    var desc: String = "",
    @Json(name = "platform")
    var platform: String = "",
    @Json(name = "category")
    var category: String = "",
    @Json(name = "deadline")
    var deadline: String = "",
    @Json(name = "progress")
    var progress: Float = 0F,
    @Json(name = "status")
    var status: String = "undone",
    @Json(name = "onPage")
    var onPage: Int = 0,
    @Json(name = "task")
    var task: Task? = null
)
