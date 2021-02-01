package com.ajailani.projekan.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Project(
    @Json(name = "id")
    var id: Int = 0,
    @Json(name = "onPage")
    var onPage: Int = 0,
    @Json(name = "itemNum")
    var itemNum: Int = 0,
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
    var progress: Int = 0,
    @Json(name = "status")
    var status: String = "undone",
    @Json(name = "hasTasks")
    var hasTasks: Boolean = false
) : Parcelable
