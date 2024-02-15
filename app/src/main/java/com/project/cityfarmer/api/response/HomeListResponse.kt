package com.project.cityfarmer.api.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class HomeListResponse(
    val data: List<Daum>,
)

data class Daum(
    val type: String,
    val tasks: List<Task>,
)

data class Task(
    val id: Int,
    val plant: PlantInfo,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val type: String,
    val deadline: Date,
    @SerializedName("complete_at")
    val completeAt: Date?,
    @SerializedName("is_complete")
    val isComplete: Boolean,
)

data class PlantInfo(
    val id: Int,
    @SerializedName("main_image")
    val mainImage: Any?,
    val nickname: String,
    @SerializedName("plant_type_name")
    val plantTypeName: String,
)