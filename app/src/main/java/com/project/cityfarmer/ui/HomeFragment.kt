package com.project.cityfarmer.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.cityfarmer.api.ApiClient
import com.project.cityfarmer.api.TokenManager
import com.project.cityfarmer.api.request.LoginRequest
import com.project.cityfarmer.api.response.HomeListResponse
import com.project.cityfarmer.api.response.LoginResponse
import com.project.cityfarmer.databinding.FragmentHomeBinding
import com.project.cityfarmer.ui.adapter.HomeTodoAdapter
import com.project.cityfarmer.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    lateinit var fragmentHomeBinding: FragmentHomeBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentHomeBinding.run {
            textViewHomeNickName.text = MyApplication.loginNickName

//            val listAdapter = HomeTodoAdapter() // 어댑터
        }

        return fragmentHomeBinding.root
    }

    override fun onResume() {
        super.onResume()
        getHomeTodo()
        mainActivity.hideBottomNavigation(false)
    }

    fun getHomeTodo() {
        var apiClient = ApiClient(mainActivity)

        var tokenManager = TokenManager(requireContext())

        apiClient.apiService.getHomeList("Bearer ${tokenManager.getAccessToken().toString()}")?.enqueue(object : Callback<HomeListResponse> {
            override fun onResponse(call: Call<HomeListResponse>, response: Response<HomeListResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: HomeListResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    fragmentHomeBinding.run {
                        var listAdapter = HomeTodoAdapter(result!!, mainActivity)
                        recyclerViewHomeTodo.run {
                            adapter = listAdapter
                            layoutManager = LinearLayoutManager(context)

                            listAdapter.itemClickListener =
                                object : HomeTodoAdapter.OnItemClickListener {
                                    override fun onItemClick(position: Int) {
                                        val mainIntent =
                                            Intent(mainActivity, PlantDetailActivity::class.java)
                                        startActivity(mainIntent)
                                    }
                                }
                        }
                    }

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: HomeListResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<HomeListResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }
}