package com.project.cityfarmer.api.request

import okhttp3.MultipartBody
import java.io.File

data class ChangeFarmImageRequest(
    val farm_image : MultipartBody.Part
)
