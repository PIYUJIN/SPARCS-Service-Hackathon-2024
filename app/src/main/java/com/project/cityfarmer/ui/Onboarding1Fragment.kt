package com.project.cityfarmer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.cityfarmer.databinding.FragmentOnboarding1Binding

class Onboarding1Fragment : Fragment() {

    lateinit var fragmentOnboarding1Binding: FragmentOnboarding1Binding
    lateinit var onboardingActivity: OnboardingActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentOnboarding1Binding = FragmentOnboarding1Binding.inflate(layoutInflater)
        onboardingActivity = activity as OnboardingActivity

        fragmentOnboarding1Binding.run {
            buttonStart.setOnClickListener {
                onboardingActivity.replaceFragment(OnboardingActivity.REGISTER_NICK_NAME_FRAGMENT, true, null)
            }
            buttonLogin.setOnClickListener {
                onboardingActivity.replaceFragment(OnboardingActivity.LOGIN_FRAGMENT, true, null)
            }
        }
        return fragmentOnboarding1Binding.root
    }
}