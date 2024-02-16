package com.project.cityfarmer.ui

import android.Manifest
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.project.cityfarmer.R
import com.project.cityfarmer.api.TokenManager
import com.project.cityfarmer.databinding.ActivityPlantDetailBinding
import com.project.cityfarmer.utils.MyApplication
import java.lang.ref.WeakReference

class PlantDetailActivity : AppCompatActivity() {

    lateinit var activityPlantDetailBinding: ActivityPlantDetailBinding

    private var uploadMessage: ValueCallback<Array<Uri>>? = null

    private val fileChooserCallback = 				//launch할 callback 설정
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it == null) {  						// 아무것도 선택하지 않았을때
                uploadMessage?.onReceiveValue(null)
                uploadMessage = null
                return@registerForActivityResult
            }
            try {
                uploadMessage?.onReceiveValue(arrayOf(it))
                uploadMessage = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPlantDetailBinding = ActivityPlantDetailBinding.inflate(layoutInflater)

        var tokenManager = TokenManager(this)

        activityPlantDetailBinding.run {
            buttonBack.setOnClickListener {
                finish()
            }
        }

        activityPlantDetailBinding.webViewPlantDetail.apply {
            webChromeClient = BodyWebChromeClient(this@PlantDetailActivity)
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.allowContentAccess = true
            settings.allowFileAccess = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.loadsImagesAutomatically = true // 웹뷰가 앱에 등록되어 있는 이미지 리소스를 자동으로 로드
        }

        activityPlantDetailBinding.webViewPlantDetail.loadUrl("https://city-farmer.vercel.app/plant/detail/${MyApplication.plantId}?token=${tokenManager.getAccessToken()}")

        setContentView(activityPlantDetailBinding.root)
    }

    class BodyWebChromeClient(activity: PlantDetailActivity) : WebChromeClient() {
        private val activityRef = WeakReference(activity)

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            val activity = activityRef.get() ?: return false
            fileChooserParams?.acceptTypes?.get(0)?.toString() ?: return false
            activity.checkPermission(filePathCallback)     //권한 검사 시작
            return true
        }
    }

    //TedPermission 이용
    private fun checkPermission(filePathCallback: ValueCallback<Array<Uri>>?) {
        TedPermission.Builder()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    if (uploadMessage != null) {
                        uploadMessage?.onReceiveValue(null)
                    }
                    uploadMessage = filePathCallback
                    fileChooserCallback.launch(arrayOf("*/*"))
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(this@PlantDetailActivity, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            })
            .setDeniedMessage("이 권한이 없으면 업로드할 수 없습니다.")
            .setPermissions(
                Manifest.permission.INTERNET
            ).check()
    }

}