package com.project.cityfarmer.api.response

data class LoginResponse(
    val username: String?,
    val access: String?,
    val refresh: String?,
    val error: String?
)