package com.project.cityfarmer.api

import android.content.Context
import com.google.gson.Gson
import com.project.cityfarmer.BuildConfig
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager

class ApiClient(val context: Context) {

    val gson = Gson()

    // TokenInterceptor 클래스를 인스턴스화
//    val tokenInterceptor = TokenInterceptor()

    val logger = HttpLoggingInterceptor().apply {
        level =
            HttpLoggingInterceptor.Level.BASIC
    }
    private val interceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttpClient에 TokenInterceptor를 추가
    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(logger)
        .cookieJar(JavaNetCookieJar(CookieManager()))
        .build()

    val retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.server_url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiService = retrofit.create(ApiService::class.java)
}