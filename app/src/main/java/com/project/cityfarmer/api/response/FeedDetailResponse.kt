package com.project.cityfarmer.api.response

import com.google.gson.annotations.SerializedName

data class FeedDetailResponse(
    val id: Int,
    val type: String,
    val title: String,
    val description: String,
    val tags: List<String>,
    val image: String,
    val username: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("my_farm_item_count")
    val myFarmItemCount: Int,
    @SerializedName("like_count")
    val likeCount: Int,
    @SerializedName("comment_count")
    val commentCount: Int,
    @SerializedName("tagged_item_count")
    val taggedItemCount: Int,
    @SerializedName("is_liked")
    val isLiked: Boolean,
    val comments: List<Comment>,
)

data class Comment(
    val id: Long,
    val description: String,
    val username: String,
)
