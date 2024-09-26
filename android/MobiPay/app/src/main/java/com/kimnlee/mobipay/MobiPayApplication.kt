package com.kimnlee.mobipay

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.util.Log
import com.google.firebase.FirebaseApp
import com.kimnlee.firebase.FCMService
import com.kimnlee.common.auth.KakaoSdkInitializer
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp

private const val TAG = "MobiPayApplication"
class MobiPayApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 카카오 SDK 초기화
        KakaoSdkInitializer.initialize(this)

        Log.d(TAG, "[모비페이] onCreate: FCM init")
        FirebaseApp.initializeApp(this)

        val fcmService = FCMService()

        fcmService.getToken { token ->
            Log.d(TAG, "이 기기의 FCM 토큰: $token")
        }

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