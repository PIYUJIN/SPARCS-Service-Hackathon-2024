package com.project.cityfarmer.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.cityfarmer.api.ApiClient
import com.project.cityfarmer.api.request.SignUpRequest
import com.project.cityfarmer.api.response.SignUpResponse
import com.project.cityfarmer.databinding.FragmentRecheckPasswordBinding
import com.project.cityfarmer.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecheckPasswordFragment : Fragment() {

    lateinit var fragmentRecheckPasswordBinding: FragmentRecheckPasswordBinding
    lateinit var onboardingActivity: OnboardingActivity

    var isSame = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentRecheckPasswordBinding = FragmentRecheckPasswordBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        fragmentRecheckPasswordBinding.run {
            textViewWrongPassword.visibility = View.INVISIBLE
            editTextPasswordRecheck.addTextChangedListener (object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // 텍스트 변경 전에 호출되는 메서드
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 텍스트 변경 중에 호출되는 메서드
                }

                override fun afterTextChanged(s: Editable?) {
                    checkPassword()
                }
            })

            buttonNext.setOnClickListener {
                if(isSame) {
                    signUp()
                }
            }

            buttonReRegisterPassword.setOnClickListener {
                onboardingActivity.removeFragment(OnboardingActivity.RECHECK_PASSWORD_FRAGMENT)
            }
        }

        return fragmentRecheckPasswordBinding.root
    }

    fun checkPassword() {
        fragmentRecheckPasswordBinding.run {
            val userPasswordCheck = editTextPasswordRecheck.text.toString()
            // 텍스트 변경 후에 호출되는 메서드
            if(userPasswordCheck != "") {
                if (MyApplication.signUpPassword != userPasswordCheck) {
                    textViewWrongPassword.visibility = View.VISIBLE
                    isSame = false
                } else {
                    textViewWrongPassword.visibility = View.INVISIBLE
                    isSame = true
                }
            }
        }
    }

    fun signUp() {
        var apiClient = ApiClient(onboardingActivity)

        var inputPasswordCheck = fragmentRecheckPasswordBinding.editTextPasswordRecheck.text.toString()

        var SignUp = SignUpRequest(
            MyApplication.signUpNickName,
            MyApplication.signUpPassword,
            inputPasswordCheck
        )

        apiClient.apiService.signUp(SignUp)?.enqueue(object :
            Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: SignUpResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    fragmentRecheckPasswordBinding.textViewWrongPassword.visibility = View.INVISIBLE
                    onboardingActivity.replaceFragment(OnboardingActivity.ONBOARDING2_FRAGMENT, true, null)
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: SignUpResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")

                    if(response.code() == 400) {
                        fragmentRecheckPasswordBinding.textViewWrongPassword.visibility =
                            View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }

}