package com.project.cityfarmer.api.request

data class SignUpRequest(
    val username : String,
    val password : String,
    val password2 : String
)