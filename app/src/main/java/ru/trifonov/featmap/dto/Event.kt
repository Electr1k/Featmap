package ru.trifonov.featmap.dto

import com.google.firebase.database.IgnoreExtraProperties
import com.yandex.mapkit.geometry.Point
import java.util.Date


@IgnoreExtraProperties
data class Event(
    val id: Int? = 0,
    val title: String? = "",
    val subtitle: String? = "",
    val images: List<String> = emptyList(),
    val startDate: String? = "",
    val endDate: String? = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val users: MutableList<String> = mutableListOf<String>()
)