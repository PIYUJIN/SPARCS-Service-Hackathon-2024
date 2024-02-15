package com.project.cityfarmer.api.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class FarmPlantListResponse(
    @SerializedName("farm_image")
    val farmImage: String,
    val plants: List<Plant>,
    val username: String?,
)

data class Plant(
    val id: Long,
    val nickname: String,
    @SerializedName("main_image")
    val mainImage: String,
    @SerializedName("start_at")
    val startAt: Date,
    @SerializedName("plant_type_name")
    val plantTypeName: String,
    @SerializedName("last_watered_at")
    val lastWateredAt: Date,
    @SerializedName("last_repotted_at")
    val lastRepottedAt: Date,
)