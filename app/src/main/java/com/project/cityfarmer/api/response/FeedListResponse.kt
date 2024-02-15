package com.project.cityfarmer.api.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class FeedListResponse(
    var data: List<Feed>
)

data class Feed(
    val id: Int,
    val title: String,
    val description: String,
    val type: String,
    val location: String,
    val user: Int,
    val image: String,
    val tags: List<String>,
    @SerializedName("like_count")
    val likeCount: Int,
    @SerializedName("comment_count")
    val commentCount: Int,
    @SerializedName("created_at")
    val createdAt: String
)
