package com.weedow.searchy.sample.mongodb.model

data class RoomDescription(
    val area: Double? = null,
    val floor: String? = null,
    val windows: Int? = null,
    val type: RoomType? = null
) {
}

enum class RoomType {
    BEDROOM,
    KITCHEN,
    DININGROOM,
    BATHROOM,
    OFFICE,
    RESTROOM
}