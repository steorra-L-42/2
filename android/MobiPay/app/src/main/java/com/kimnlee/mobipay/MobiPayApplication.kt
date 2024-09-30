package com.kimnlee.mobipay

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.util.Log
import com.google.firebase.FirebaseApp
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.firebase.FCMService
import com.kimnlee.common.auth.KakaoSdkInitializer
import com.kimnlee.common.network.ApiClient
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.naver.maps.map.NaverMapSdk

private const val TAG = "MobiPayApplication"
class MobiPayApplication : Application() {

    lateinit var authManager: AuthManager
    lateinit var apiClient: ApiClient
    lateinit var fcmService: FCMService

    override fun onCreate() {
        super.onCreate()

        // ApiClient 초기화
//        apiClient = ApiClient.getInstance()

        // AuthManager 초기화
        authManager = AuthManager(this)
//        apiClient = ApiClient.getInstance(authManager)
        apiClient = ApiClient.getInstance { authManager.getAuthToken() }

        // 카카오 SDK 초기화
        KakaoSdkInitializer.initialize(this)

        Log.d(TAG, "[모비페이] onCreate: FCM init")
        FirebaseApp.initializeApp(this)

        fcmService = FCMService(apiClient)

        fcmService.getToken { token ->
            Log.d(TAG, "이 기기의 FCM 토큰: $token")
        }

//        val naverMapClientSecret = BuildConfig.NAVER_MAP_CLIENT_SECRET
//        Log.d(TAG, "onCreate: 네이버 Client Secret ${naverMapClientSecret}")
//        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient(naverMapClientSecret)
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient("81dn8nvzim")

        createNotificationChannel()
        // Setup MapboxNavigation
        MapboxNavigationApp.setup(

            NavigationOptions.Builder(applicationContext)
                .accessToken(getString(com.kimnlee.common.R.string.mapbox_access_token))
                .build()
        ).attachAllActivities(this)

    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "payment_request",
            "결제요청채널",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "MobiPay 결제 알림을 처리하기 위한 채널이에요."
            enableVibration(true)
            setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
            )
        }

        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}