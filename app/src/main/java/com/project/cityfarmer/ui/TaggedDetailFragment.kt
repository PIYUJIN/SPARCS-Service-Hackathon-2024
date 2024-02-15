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
import com.project.cityfarmer.api.response.FarmPlantTaggedListResponse
import com.project.cityfarmer.databinding.FragmentTaggedDetailBinding
import com.project.cityfarmer.ui.adapter.FarmPlantAdapter
import com.project.cityfarmer.ui.adapter.FarmPlantTaggedAdapter
import com.project.cityfarmer.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaggedDetailFragment : Fragment() {

    lateinit var fragmentTaggedDetailBinding: FragmentTaggedDetailBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentTaggedDetailBinding = FragmentTaggedDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentTaggedDetailBinding.run {
            buttonBack.setOnClickListener {
                mainActivity.removeFragment(MainActivity.TAGGED_DETAIL_FRAGMNET)
            }
        }

        return fragmentTaggedDetailBinding.root
    }

    override fun onResume() {
        super.onResume()
        getTaggedPlantList()
        mainActivity.hideBottomNavigation(true)
    }

    fun getTaggedPlantList() {
        var apiClient = ApiClient(requireContext())

        var tokenManager = TokenManager(requireContext())

        apiClient.apiService.getFarmPlantTagged(MyApplication.feedId)
            ?.enqueue(object :
                Callback<FarmPlantTaggedListResponse> {
                override fun onResponse(
                    call: Call<FarmPlantTaggedListResponse>,
                    response: Response<FarmPlantTaggedListResponse>
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        var result: FarmPlantTaggedListResponse? = response.body()
                        Log.d("##", "onResponse 성공: " + result?.toString())

                        fragmentTaggedDetailBinding.run {
                            recyclerViewTaggedPlant.run {
                                adapter = FarmPlantTaggedAdapter(result!!, mainActivity)
                                layoutManager = LinearLayoutManager(context)
                            }
                        }

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: FarmPlantTaggedListResponse? = response.body()
                        Log.d("##", "onResponse 실패")
                        Log.d("##", "onResponse 실패: " + response.code())
                        Log.d("##", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("##", "Error Response: $errorBody")
                    }
                }

                override fun onFailure(call: Call<FarmPlantTaggedListResponse>, t: Throwable) {
                    // 통신 실패
                    Log.d("##", "onFailure 에러: " + t.message.toString());
                }
            })
    }
}