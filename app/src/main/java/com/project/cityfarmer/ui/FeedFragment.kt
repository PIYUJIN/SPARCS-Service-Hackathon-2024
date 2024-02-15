package com.project.cityfarmer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.cityfarmer.R
import com.project.cityfarmer.api.ApiClient
import com.project.cityfarmer.api.TokenManager
import com.project.cityfarmer.api.response.FeedListResponse
import com.project.cityfarmer.api.response.HomeListResponse
import com.project.cityfarmer.databinding.FragmentFeedBinding
import com.project.cityfarmer.ui.adapter.FeedListAdapter
import com.project.cityfarmer.ui.adapter.HomeTodoAdapter
import com.project.cityfarmer.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedFragment : Fragment() {

    lateinit var fragmentFeedBinding: FragmentFeedBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentFeedBinding = FragmentFeedBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentFeedBinding.run {

            var typeArray = resources.getStringArray(R.array.feed_type_array)	// 배열
            setSpinner(spinnerTag1, typeArray)	// 스피너 설정

            var placeArray = resources.getStringArray(R.array.feed_place_array)	// 배열
            setSpinner(spinnerTag2, placeArray)	// 스피너 설정

            var plantArray = resources.getStringArray(R.array.feed_plant_array)	// 배열
            setSpinner(spinnerTag3, plantArray)	// 스피너 설정

            spinnerTag1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    fragmentFeedBinding.spinnerTag1.setBackgroundResource(R.drawable.feed_spinner_background)
                    when(position) {
                        0 -> {
                            MyApplication.feedTagType = "전체"
                            selectTag()
                            fragmentFeedBinding.spinnerTag1.setBackgroundResource(R.drawable.row_background)
                        }
                        1 -> {
                            MyApplication.feedTagType = resources.getStringArray(R.array.feed_type_array).get(1)
                            selectTag()
                        }
                        2 -> {
                            MyApplication.feedTagType = resources.getStringArray(R.array.feed_type_array).get(2)
                            selectTag()
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    MyApplication.feedTagType = "전체"
                }
            }

            spinnerTag2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    fragmentFeedBinding.spinnerTag2.setBackgroundResource(R.drawable.feed_spinner_background)
                    when(position) {
                        0 -> {
                            MyApplication.feedTagPlace = "전체"
                            selectTag()
                            fragmentFeedBinding.spinnerTag2.setBackgroundResource(R.drawable.row_background)
                        }

                        1 -> {
                            MyApplication.feedTagPlace =
                                resources.getStringArray(R.array.feed_place_array).get(1)
                            selectTag()
                        }

                        2 -> {
                            MyApplication.feedTagPlace =
                                resources.getStringArray(R.array.feed_place_array).get(2)
                            selectTag()
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    MyApplication.feedTagType = "전체"
                }
            }

            spinnerTag3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    fragmentFeedBinding.spinnerTag3.setBackgroundResource(R.drawable.feed_spinner_background)
                    when(position) {
                        0 -> {
                            MyApplication.feedTagPlant = "전체"
                            selectTag()
                            fragmentFeedBinding.spinnerTag3.setBackgroundResource(R.drawable.row_background)
                        }
                        1 -> {
                            MyApplication.feedTagPlant = resources.getStringArray(R.array.feed_plant_array).get(1)
                            selectTag()
                        }
                        2 -> {
                            MyApplication.feedTagPlant = resources.getStringArray(R.array.feed_plant_array).get(2)
                            selectTag()
                        }
                        3 -> {
                            MyApplication.feedTagPlant = resources.getStringArray(R.array.feed_plant_array).get(3)
                            selectTag()
                        }
                        4 -> {
                            MyApplication.feedTagPlant = resources.getStringArray(R.array.feed_plant_array).get(4)
                            selectTag()
                        }
                        5 -> {
                            MyApplication.feedTagPlant = resources.getStringArray(R.array.feed_plant_array).get(5)
                            selectTag()
                        }
                        6 -> {
                            MyApplication.feedTagPlant = resources.getStringArray(R.array.feed_plant_array).get(6)
                            selectTag()
                        }
                        7 -> {
                            MyApplication.feedTagPlant = resources.getStringArray(R.array.feed_plant_array).get(7)
                            selectTag()
                        }
                        8 -> {
                            MyApplication.feedTagPlant = resources.getStringArray(R.array.feed_plant_array).get(8)
                            selectTag()
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    MyApplication.feedTagType = "전체"
                }
            }
        }

        return fragmentFeedBinding.root
    }

    override fun onResume() {
        super.onResume()
        selectTag()
        mainActivity.hideBottomNavigation(false)
    }

    // 스피너 설정
    private fun setSpinner(spinner: Spinner, array: Array<String>) {
        var adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line
        ) { override fun getCount(): Int =  super.getCount() }  // array에서 hint 안 보이게 하기
        adapter.addAll(array.toMutableList())   // 배열 추가
        spinner.adapter = adapter               // 어댑터 달기
        spinner.setSelection(0)    // 스피너 초기값=hint
    }

    fun selectTag() {
        getFeedList()
        Log.d("##","type : ${MyApplication.feedTagPlant}")
    }

    fun getFeedList() {
        var apiClient = ApiClient(requireContext())

        apiClient.apiService.getFeedList(MyApplication.feedTagType, MyApplication.feedTagPlace, MyApplication.feedTagPlant)?.enqueue(object :
            Callback<FeedListResponse> {
            override fun onResponse(call: Call<FeedListResponse>, response: Response<FeedListResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: FeedListResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    fragmentFeedBinding.run {
                        recyclerViewFeed.run {

                            adapter = FeedListAdapter(result!!, mainActivity)
                            layoutManager = LinearLayoutManager(context)
                        }
                    }

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: FeedListResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<FeedListResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }
}