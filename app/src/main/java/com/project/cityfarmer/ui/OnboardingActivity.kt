package com.project.cityfarmer.ui

import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.transition.MaterialSharedAxis
import com.project.cityfarmer.R
import com.project.cityfarmer.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    lateinit var activityOnboardingBinding: ActivityOnboardingBinding

    var newFragment: Fragment? = null
    var oldFragment: Fragment? = null


    companion object {
        val ONBOARDING1_FRAGMENT = "Onboarding1Fragment"
        val REGISTER_NICK_NAME_FRAGMENT = "RegisterNickNameFragment"
        val REGISTER_PASSWORD_FRAGMENT = "RegisterPasswordFragment"
        val RECHECK_PASSWORD_FRAGMENT = "RecheckPasswordFragment"
        val ONBOARDING2_FRAGMENT = "Onboarding2Fragment"
        val LOGIN_FRAGMENT = "LoginFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityOnboardingBinding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(activityOnboardingBinding.root)

        replaceFragment(ONBOARDING1_FRAGMENT, true, null)

    }


    // 지정한 Fragment를 보여주는 메서드
    fun replaceFragment(name:String, addToBackStack:Boolean, bundle: Bundle?){

        SystemClock.sleep(200)

        // Fragment 교체 상태로 설정
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        if(newFragment != null){
            oldFragment = newFragment
        }

        // 새로운 Fragment를 담을 변수
        var newFragment = when (name) {
            ONBOARDING1_FRAGMENT -> {
                Onboarding1Fragment()
            }
            REGISTER_NICK_NAME_FRAGMENT -> {
                RegisterNickNameFragment()
            }
            REGISTER_PASSWORD_FRAGMENT -> {
                RegisterPasswordFragment()
            }
            RECHECK_PASSWORD_FRAGMENT -> {
                RecheckPasswordFragment()
            }
            ONBOARDING2_FRAGMENT -> {
                Onboarding2Fragment()
            }
            LOGIN_FRAGMENT -> {
                LoginFragment()
            }
            else -> {
                Fragment()
            }
        }

        newFragment?.arguments = bundle

        if(newFragment != null) {

            // 애니메이션 설정
            if(oldFragment != null){
                oldFragment?.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                oldFragment?.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                oldFragment?.enterTransition = null
                oldFragment?.returnTransition = null
            }

            newFragment?.exitTransition = null
            newFragment?.reenterTransition = null
            newFragment?.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            newFragment?.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

            // Fragment 교체
            fragmentTransaction.replace(R.id.fragmentContainerViewOnboarding, newFragment!!)

            if (addToBackStack == true) {
                // Fragment를 Backstack에 넣어 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령이 동작하도록 한다.
            fragmentTransaction.commit()
        }
    }

    fun removeFragment(name:String){
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}