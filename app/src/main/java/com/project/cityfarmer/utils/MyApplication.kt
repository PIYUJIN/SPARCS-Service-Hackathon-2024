package com.project.cityfarmer.utils

import android.app.Application

class MyApplication : Application() {
    companion object {

        var signUpNickName = ""
        var signUpPassword = ""
        var loginNickName = ""

        var feedTagType = "전체"
        var feedTagPlace = "전체"
        var feedTagPlant = "전체"

        var feedId = 0
        var feedUserId = 0

        var plantId = 0
    }
}