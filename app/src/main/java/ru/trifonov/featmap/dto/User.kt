package ru.trifonov.featmap.dto

import com.google.firebase.database.IgnoreExtraProperties
import com.yandex.mapkit.geometry.Point


@IgnoreExtraProperties
data class User(
    var name: String? = null,
    var phoneNumber: String? = null,
//    var location: Point? = null,
    var uid: String? = null,
    var eventsList: ArrayList<Int>? = null
//    var linkImage: String? = null,
)