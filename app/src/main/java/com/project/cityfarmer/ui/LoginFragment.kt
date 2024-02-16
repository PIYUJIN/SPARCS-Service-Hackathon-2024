package com.project.cityfarmer.ui

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.cityfarmer.R
import com.project.cityfarmer.api.ApiClient
import com.project.cityfarmer.api.TokenManager
import com.project.cityfarmer.api.request.ChangeFarmImageRequest
import com.project.cityfarmer.api.request.LoginRequest
import com.project.cityfarmer.api.response.ChangeFarmImageResponse
import com.project.cityfarmer.api.response.LoginResponse
import com.project.cityfarmer.databinding.FragmentLoginBinding
import com.project.cityfarmer.utils.MyApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class LoginFragment : Fragment() {

    lateinit var fragmentLoginBinding: FragmentLoginBinding
    lateinit var onboardingActivity: OnboardingActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentLoginBinding = FragmentLoginBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        fragmentLoginBinding.run {
            textViewWrongUser.visibility = View.INVISIBLE

            buttonLogin.setOnClickListener {
                login()
            }
            buttonSignUp.setOnClickListener {
                onboardingActivity.removeFragment(OnboardingActivity.LOGIN_FRAGMENT)
            }
        }
        return fragmentLoginBinding.root
    }

    fun login() {
        var apiClient = ApiClient(onboardingActivity)

        var nickName = fragmentLoginBinding.editTextNickName.text.toString()
        var password = fragmentLoginBinding.editTextPassword.text.toString()

        var Login = LoginRequest(nickName, password)
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

                    fragmentLoginBinding.textViewWrongUser.visibility = View.INVISIBLE
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
                        fragmentLoginBinding.textViewWrongUser.visibility = View.VISIBLE
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