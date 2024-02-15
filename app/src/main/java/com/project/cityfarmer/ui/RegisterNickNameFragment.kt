package com.project.cityfarmer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.cityfarmer.api.ApiClient
import com.project.cityfarmer.api.request.CheckUserNameRequest
import com.project.cityfarmer.api.response.CheckUserNameResponse
import com.project.cityfarmer.databinding.FragmentRegisterNickNameBinding
import com.project.cityfarmer.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterNickNameFragment : Fragment() {

    lateinit var fragmentRegisterNickNameBinding: FragmentRegisterNickNameBinding
    lateinit var onboardingActivity: OnboardingActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("화면 전환", "registerNickName")

        fragmentRegisterNickNameBinding = FragmentRegisterNickNameBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        fragmentRegisterNickNameBinding.run {
            textViewSameNickName.visibility = View.INVISIBLE
            buttonNext.setOnClickListener {
                checkNickName()
            }
        }

        return fragmentRegisterNickNameBinding.root
    }

    fun checkNickName() {
        var apiClient = ApiClient(onboardingActivity)

        var inputNickName = fragmentRegisterNickNameBinding.editTextNickName.text.toString()

        var NickName = CheckUserNameRequest(inputNickName)

        apiClient.apiService.checkNickName(NickName)?.enqueue(object :
            Callback<CheckUserNameResponse> {
            override fun onResponse(call: Call<CheckUserNameResponse>, response: Response<CheckUserNameResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: CheckUserNameResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    fragmentRegisterNickNameBinding.textViewSameNickName.visibility = View.INVISIBLE
                    MyApplication.signUpNickName = inputNickName
                    onboardingActivity.replaceFragment(OnboardingActivity.REGISTER_PASSWORD_FRAGMENT, true, null)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: CheckUserNameResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    if(response.code() == 400) {
                        fragmentRegisterNickNameBinding.textViewSameNickName.visibility =
                            View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<CheckUserNameResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }
}