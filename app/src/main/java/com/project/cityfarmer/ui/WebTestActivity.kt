package com.project.cityfarmer.ui

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.project.cityfarmer.api.TokenManager
import com.project.cityfarmer.databinding.ActivityWebTestBinding
import java.lang.ref.WeakReference

class WebTestActivity : AppCompatActivity() {

    lateinit var activityWebViewBinding: ActivityWebTestBinding

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

        activityWebViewBinding = ActivityWebTestBinding.inflate(layoutInflater)
        var tokenManager = TokenManager(this)

        activityWebViewBinding.run {
            buttonBack.setOnClickListener {
                finish()
            }
        }

        activityWebViewBinding.webViewPlantRegister.apply {
            webChromeClient = BodyWebChromeClient(this@WebTestActivity)
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.allowContentAccess = true
            settings.allowFileAccess = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.loadsImagesAutomatically = true // 웹뷰가 앱에 등록되어 있는 이미지 리소스를 자동으로 로드
        }

        activityWebViewBinding.webViewPlantRegister.loadUrl("https://city-farmer.vercel.app/plant/register/image?token=${tokenManager.getAccessToken()}")

        setContentView(activityWebViewBinding.root)
    }

    class BodyWebChromeClient(activity: WebTestActivity) : WebChromeClient() {
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
                    Toast.makeText(this@WebTestActivity, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            })
            .setDeniedMessage("이 권한이 없으면 이력서를 업로드할 수 없습니다.")
            .setPermissions(
                Manifest.permission.INTERNET
            ).check()
    }

}