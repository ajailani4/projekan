package com.ajailani.projekan.data.model

import com.squareup.moshi.Json

data class Project(
    @Json(name = "id")
    val id: Int,
    @Json(name = "icon")
    val icon: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "desc")
    val desc: String,
    @Json(name = "platform")
    val platform: String,
    @Json(name = "category")
    val category: String,
    @Json(name = "deadline")
    val deadline: String,
    @Json(name = "progress")
    val progress: Float,
    @Json(name = "task")
    val task: Task?
)
