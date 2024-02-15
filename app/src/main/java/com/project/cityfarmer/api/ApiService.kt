package com.project.cityfarmer.api

import com.project.cityfarmer.api.request.ChangeFarmImageRequest
import com.project.cityfarmer.api.request.CheckUserNameRequest
import com.project.cityfarmer.api.request.LoginRequest
import com.project.cityfarmer.api.request.SignUpRequest
import com.project.cityfarmer.api.response.ChangeFarmImageResponse
import com.project.cityfarmer.api.response.CheckUserNameResponse
import com.project.cityfarmer.api.response.CompleteTodoResponse
import com.project.cityfarmer.api.response.FarmPlantListResponse
import com.project.cityfarmer.api.response.HomeListResponse
import com.project.cityfarmer.api.response.LoginResponse
import com.project.cityfarmer.api.response.SignUpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // 중복 닉네임 확인
    @POST("/auth/check-username")
    fun checkNickName(
        @Body parameters: CheckUserNameRequest
    ): Call<CheckUserNameResponse>

    // 회원가입
    @POST("/auth/signup")
    fun signUp(
        @Body parameters: SignUpRequest
    ): Call<SignUpResponse>

    // 로그인
    @POST("/auth/login")
    fun login(
        @Body parameters: LoginRequest
    ): Call<LoginResponse>

    // 홈화면 TODO
    @GET("/plant/log/my")
    fun getHomeList(
        @Header("Authorization") token: String
    ): Call<HomeListResponse>

    // 홈화면 TODO 완료처리
    @PATCH("/plant/log/complete/{pk}")
    fun checkTodo(
        @Header("Authorization") token: String,
        @Path("pk") pk:Int
    ): Call<HomeListResponse>

    // 나의 밭 작물 목록
    @GET("/farm/my")
    fun getFarmList(
        @Header("Authorization") token: String
    ): Call<FarmPlantListResponse>

    // 나의 밭 사진 변경
    @GET("/farm/image")
    fun changeFarmImage(
        @Header("Authorization") token: String,
        @Body parameters: ChangeFarmImageRequest
    ): Call<ChangeFarmImageResponse>
}