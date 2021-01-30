package com.ajailani.projekan.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    @Json(name = "id")
    var id: String? = "",
    @Json(name = "title")
    var title: String = "",
    @Json(name = "status")
    var status: String = "undone"
) : Parcelable