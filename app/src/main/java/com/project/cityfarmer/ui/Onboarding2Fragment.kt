package com.project.cityfarmer.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.cityfarmer.api.ApiClient
import com.project.cityfarmer.api.TokenManager
import com.project.cityfarmer.api.request.LoginRequest
import com.project.cityfarmer.api.response.LoginResponse
import com.project.cityfarmer.databinding.FragmentOnboarding2Binding
import com.project.cityfarmer.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Onboarding2Fragment : Fragment() {

    lateinit var fragmentOnboarding2Binding: FragmentOnboarding2Binding
    lateinit var onboardingActivity: OnboardingActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentOnboarding2Binding = FragmentOnboarding2Binding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        fragmentOnboarding2Binding.run {
            textViewPasswordTitle.text = "회원가입 완료!\n축하합니다. ${MyApplication.signUpNickName}님!"
            buttonNext.setOnClickListener {
                login()
            }
        }
        return fragmentOnboarding2Binding.root
    }

    fun login() {
        var apiClient = ApiClient(onboardingActivity)

        var Login = LoginRequest(MyApplication.signUpNickName, MyApplication.signUpPassword)
        var tokenManager = TokenManager(requireContext())

        apiClient.apiService.login(Login)?.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: LoginResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    tokenManager.saveTokens(result?.access.toString(), result?.refresh.toString())
                    Log.d("##", "${tokenManager.getAccessToken()}")
                    Log.d("##", "${tokenManager.getRefreshToken()}")

                    MyApplication.loginNickName = result?.username.toString()

                    onboardingActivity.finish()
                    val mainIntent = Intent(onboardingActivity, MainActivity::class.java)
                    startActivity(mainIntent)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: LoginResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    if(response.code() == 400) {
                        onboardingActivity.removeFragment(OnboardingActivity.ONBOARDING2_FRAGMENT)
                        onboardingActivity.removeFragment(OnboardingActivity.RECHECK_PASSWORD_FRAGMENT)
                        onboardingActivity.removeFragment(OnboardingActivity.REGISTER_PASSWORD_FRAGMENT)
                        onboardingActivity.removeFragment(OnboardingActivity.REGISTER_NICK_NAME_FRAGMENT)
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }
}