package com.project.cityfarmer.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.project.cityfarmer.R
import com.project.cityfarmer.api.ApiClient
import com.project.cityfarmer.api.TokenManager
import com.project.cityfarmer.api.response.FarmPlantListResponse
import com.project.cityfarmer.databinding.FragmentFarmDetailBinding
import com.project.cityfarmer.ui.adapter.FarmPlantAdapter
import com.project.cityfarmer.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FarmDetailFragment : Fragment() {

    lateinit var fragmentFarmDetailBinding: FragmentFarmDetailBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentFarmDetailBinding = FragmentFarmDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentFarmDetailBinding.run {
            buttonBack.setOnClickListener {
                mainActivity.removeFragment(MainActivity.FARM_DETAIL_FRAGMENT)
            }
        }

        return fragmentFarmDetailBinding.root
    }

    override fun onResume() {
        super.onResume()
        getFeedFarmDetail(MyApplication.feedUserId)
        mainActivity.hideBottomNavigation(true)
    }

    fun getFeedFarmDetail(userId: Int) {
        var apiClient = ApiClient(requireContext())

        var tokenManager = TokenManager(requireContext())

        apiClient.apiService.getFarmPlant(userId)
            ?.enqueue(object :
                Callback<FarmPlantListResponse> {
                override fun onResponse(
                    call: Call<FarmPlantListResponse>,
                    response: Response<FarmPlantListResponse>
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        var result: FarmPlantListResponse? = response.body()
                        Log.d("##", "onResponse 성공: " + result?.toString())

                        Glide.with(requireContext())
                            .load(result?.farmImage)
                            .into(fragmentFarmDetailBinding.imageViewFarm)

                        fragmentFarmDetailBinding.run {
                            recyclerViewMyPlant.run {
                                adapter = FarmPlantAdapter(result!!, mainActivity)
                                layoutManager = LinearLayoutManager(context)
                            }
                            textViewTitle.text = "${result?.username}의 밭자랑"
                        }

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: FarmPlantListResponse? = response.body()
                        Log.d("##", "onResponse 실패")
                        Log.d("##", "onResponse 실패: " + response.code())
                        Log.d("##", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("##", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<FarmPlantListResponse>, t: Throwable) {
                    // 통신 실패
                    Log.d("##", "onFailure 에러: " + t.message.toString());
                }
            })
    }
}