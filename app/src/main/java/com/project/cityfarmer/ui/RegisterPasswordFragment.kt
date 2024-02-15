package com.project.cityfarmer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.cityfarmer.databinding.FragmentRegisterPasswordBinding
import com.project.cityfarmer.utils.MyApplication

class RegisterPasswordFragment : Fragment() {
    lateinit var fragmentRegisterPasswordBinding: FragmentRegisterPasswordBinding
    lateinit var onboardingActivity: OnboardingActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentRegisterPasswordBinding = FragmentRegisterPasswordBinding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        fragmentRegisterPasswordBinding.run {
            textViewPasswordTitle.text = "${MyApplication.signUpNickName} 농부님!\n나중을 위해 비밀번호를 입력해주세요."
            buttonNext.setOnClickListener {
                MyApplication.signUpPassword = editTextPassword.text.toString()
                onboardingActivity.replaceFragment(
                    OnboardingActivity.RECHECK_PASSWORD_FRAGMENT,
                    true,
                    null
                )
            }
        }

        return fragmentRegisterPasswordBinding.root
    }
}