package com.project.cityfarmer

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.transition.MaterialSharedAxis
import com.project.cityfarmer.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding

    var newFragment: Fragment? = null
    var oldFragment: Fragment? = null


    companion object {
        val HOME_FRAGMENT = "HomeFragment"
        val FARM_FRAGMENT = "FarmFragment"
        val FEED_FRAGMENT = "FeedFragment"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        activityMainBinding.run {
            bottomNavigationViewMain.run {

                setOnItemSelectedListener {
                    when(it.itemId) {
                        R.id.item_bottom_home -> {
                            replaceFragment(HOME_FRAGMENT, true, null)
                        }
                        R.id.item_bottom_farm -> {
                            replaceFragment(FARM_FRAGMENT, true, null)
                        }
                        R.id.item_bottom_feed -> {
                            replaceFragment(FEED_FRAGMENT, true, null)
                        }
                    }
                    true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activityMainBinding.bottomNavigationViewMain.selectedItemId = R.id.item_bottom_home
    }

    // 지정한 Fragment를 보여주는 메서드
    fun replaceFragment(name:String, addToBackStack:Boolean, bundle:Bundle?){

        SystemClock.sleep(200)

        // Fragment 교체 상태로 설정
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        if(newFragment != null){
            oldFragment = newFragment
        }

        // 새로운 Fragment를 담을 변수
        var newFragment = when (name) {
            HOME_FRAGMENT -> {
                HomeFragment()
            }
            FARM_FRAGMENT -> {
                FarmFragment()
            }
            FEED_FRAGMENT -> {
                FeedFragment()
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
            fragmentTransaction.replace(R.id.fragmentContainerView, newFragment!!)

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

    fun hideBottomNavigation(state:Boolean){
        if(state) activityMainBinding.bottomNavigationViewMain.visibility = View.GONE else activityMainBinding.bottomNavigationViewMain.visibility=
            View.VISIBLE
    }

    // 입력 요소에 포커스를 주는 메서드
    fun showSoftInput(view: View){
        view.requestFocus()

        val inputMethodManger = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        thread {
            SystemClock.sleep(200)
            inputMethodManger.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}