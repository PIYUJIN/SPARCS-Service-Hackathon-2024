package com.project.cityfarmer.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.project.cityfarmer.R
import com.project.cityfarmer.api.ApiClient
import com.project.cityfarmer.api.TokenManager
import com.project.cityfarmer.api.request.ChangeFarmImageRequest
import com.project.cityfarmer.api.response.ChangeFarmImageResponse
import com.project.cityfarmer.api.response.FarmPlantListResponse
import com.project.cityfarmer.api.response.HomeListResponse
import com.project.cityfarmer.databinding.FragmentFarmBinding
import com.project.cityfarmer.ui.adapter.FarmPlantAdapter
import com.project.cityfarmer.ui.adapter.HomeTodoAdapter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class FarmFragment : Fragment() {

    lateinit var fragmentFarmBinding: FragmentFarmBinding
    lateinit var mainActivity: MainActivity

    var isFabOpen = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentFarmBinding = FragmentFarmBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity


        fragmentFarmBinding.run {
            // 플로팅 버튼 클릭시 에니메이션 동작 기능
            fabMain.setOnClickListener {
                toggleFab()
            }

            // 플로팅 버튼 클릭 이벤트 - 작물 추가
            fabAdd.setOnClickListener {
                val mainIntent = Intent(mainActivity, WebTestActivity::class.java)
                startActivity(mainIntent)
//                mainActivity.replaceFragment(MainActivity.PLANT_REGISTER_WEB_VIEW_FRAGMENT, true, null)
//                Toast.makeText(requireContext(), "카메라 버튼 클릭!", Toast.LENGTH_SHORT).show()
            }

            // 플로팅 버튼 클릭 이벤트 - 사진변경
            fabPicture.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.FARM_PICTURE_EDIT_FRAGMENT, true, null)
//                Toast.makeText(requireContext(), "사진변경 버튼 클릭!", Toast.LENGTH_SHORT).show()
            }
        }
        return fragmentFarmBinding.root
    }

    override fun onResume() {
        super.onResume()
        getFarmPlantList()
        mainActivity.hideBottomNavigation(false)
    }


    /***
     *  플로팅 액션 버튼 클릭시 동작하는 애니메이션 효과 세팅
     */
    fun toggleFab() {

        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션 세팅
        if (isFabOpen) {
            ObjectAnimator.ofFloat(fragmentFarmBinding.fabAdd, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(fragmentFarmBinding.fabPicture, "translationY", 0f)
                .apply { start() }
            fragmentFarmBinding.fabMain.setImageResource(R.drawable.plus_white)

            // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션 세팅
        } else {
            ObjectAnimator.ofFloat(fragmentFarmBinding.fabAdd, "translationY", -200f)
                .apply { start() }
            ObjectAnimator.ofFloat(fragmentFarmBinding.fabPicture, "translationY", -400f)
                .apply { start() }
            fragmentFarmBinding.fabMain.setImageResource(R.drawable.close_white)
        }

        isFabOpen = !isFabOpen

    }

    fun getFarmPlantList() {
        var apiClient = ApiClient(requireContext())

        var tokenManager = TokenManager(requireContext())

        apiClient.apiService.getFarmList("Bearer ${tokenManager.getAccessToken().toString()}")
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

//                        Glide.with(requireContext())
//                            .load(result?.farmImage)
//                            .into(fragmentFarmBinding.imageViewFarm)

                        fragmentFarmBinding.run {
                            recyclerViewMyPlant.run {
                                adapter = FarmPlantAdapter(result!!, mainActivity)
                                layoutManager = LinearLayoutManager(context)
                            }
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

    fun bitmapToFile(bitmap: Bitmap, path: String): File{
        var file = File(path)
        var out: OutputStream? = null
        try{
            file.createNewFile()
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }finally{
            out?.close()
        }
        return file
    }
}