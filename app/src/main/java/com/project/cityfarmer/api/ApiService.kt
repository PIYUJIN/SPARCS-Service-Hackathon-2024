package com.project.cityfarmer.api

import com.project.cityfarmer.api.request.ChangeFarmImageRequest
import com.project.cityfarmer.api.request.CheckUserNameRequest
import com.project.cityfarmer.api.request.FeedCommentRequest
import com.project.cityfarmer.api.request.LikeFeedRequest
import com.project.cityfarmer.api.request.LoginRequest
import com.project.cityfarmer.api.request.SignUpRequest
import com.project.cityfarmer.api.response.ChangeFarmImageResponse
import com.project.cityfarmer.api.response.CheckUserNameResponse
import com.project.cityfarmer.api.response.CompleteTodoResponse
import com.project.cityfarmer.api.response.FarmPlantListResponse
import com.project.cityfarmer.api.response.FarmPlantTaggedListResponse
import com.project.cityfarmer.api.response.FeedCommentResponse
import com.project.cityfarmer.api.response.FeedDetailResponse
import com.project.cityfarmer.api.response.FeedListResponse
import com.project.cityfarmer.api.response.HomeListResponse
import com.project.cityfarmer.api.response.LikeFeedResponse
import com.project.cityfarmer.api.response.LoginResponse
import com.project.cityfarmer.api.response.SignUpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    // 피드 목록
    @GET("/diary")
    fun getFeedList(
        @Query("type") type:String,
        @Query("location") location:String,
        @Query("plant_type") plant_type:String,
    ): Call<FeedListResponse>

    // 피드 상세
    @GET("/diary/{pk}")
    fun getFeedDetail(
        @Header("Authorization") token: String,
        @Path("pk") pk:Int
    ): Call<FeedDetailResponse>

    // 피드 댓글 작성
    @POST("/comment")
    fun addComment(
        @Header("Authorization") token: String,
        @Body parameters: FeedCommentRequest
    ): Call<FeedCommentResponse>

    // 피드 밭자랑 작물 목록
    @GET("/farm/target")
    fun getFarmPlant(
        @Query("user") user:Int
    ): Call<FarmPlantListResponse>

    // 피드 태그 작물 목록
    @GET("/plant/tag")
    fun getFarmPlantTagged(
        @Query("diary") diary:Int
    ): Call<FarmPlantTaggedListResponse>

    // 피드 좋아요
    @POST("/like/")
    fun likeFeed(
        @Header("Authorization") token: String,
        @Body parameters: LikeFeedRequest
    ): Call<LikeFeedResponse>
}