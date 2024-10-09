package com.kimnlee.mobipay

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.util.Log
import com.google.firebase.FirebaseApp
import com.kimnlee.common.FCMDependencyProvider
import com.kimnlee.common.MemberInvitationOperations
import com.kimnlee.common.PaymentOperations
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.firebase.FCMService
import com.kimnlee.common.auth.KakaoSdkInitializer
import com.kimnlee.common.network.ApiClient
import com.kimnlee.common.utils.AppLifecycleTracker
import com.kimnlee.common.utils.AutoSaveParkingManager
import com.kimnlee.common.utils.MobiNotificationManager
import com.kimnlee.memberinvitation.data.api.MemberInvitationApiService
import com.kimnlee.memberinvitation.data.repository.MemberInvitationRepository
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import com.kimnlee.payment.data.api.PaymentApiService
import com.kimnlee.payment.data.repository.PaymentRepository
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.naver.maps.map.NaverMapSdk

private const val TAG = "MobiPayApplication"
class MobiPayApplication : Application(), FCMDependencyProvider {

    private lateinit var mobiNotificationManager: MobiNotificationManager
    private lateinit var authManagerInstance: AuthManager
    private lateinit var apiClientInstance: ApiClient
    lateinit var fcmService: FCMService
    private lateinit var paymentRepository: PaymentRepository
    private lateinit var memberInvitationViewModel: MemberInvitationViewModel
    private lateinit var memberInvitationRepository: MemberInvitationRepository

    lateinit var autoSaveParkingManager: AutoSaveParkingManager
        private set

    val aMemberInvitationViewModel: MemberInvitationViewModel
        get() = memberInvitationViewModel

    override val paymentOperations: PaymentOperations
        get() = paymentRepository

    override val memberInvitationOperations: MemberInvitationOperations
        get() = memberInvitationRepository

    override fun onCreate() {
        super.onCreate()

        mobiNotificationManager = MobiNotificationManager.getInstance(this)
        authManagerInstance = AuthManager(this)
        apiClientInstance = ApiClient.getInstance { authManagerInstance.getAuthToken() }

        val paymentApiService = apiClient.authenticatedApi.create(PaymentApiService::class.java)
        paymentRepository = PaymentRepository(paymentApiService, mobiNotificationManager, applicationContext)


        memberInvitationViewModel = MemberInvitationViewModel(authManager)
        val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter


        memberInvitationViewModel.initBluetoothAdapter(bluetoothAdapter)

        val memberInvitationApiService = apiClient.authenticatedApi.create(MemberInvitationApiService::class.java)
        memberInvitationRepository = MemberInvitationRepository(memberInvitationApiService, mobiNotificationManager, applicationContext, memberInvitationViewModel)

        registerActivityLifecycleCallbacks(AppLifecycleTracker())

        // 카카오 SDK 초기화
        KakaoSdkInitializer.initialize(this)

        Log.d(TAG, "[모비페이] onCreate: FCM init")
        FirebaseApp.initializeApp(this)

        fcmService = FCMService()

        fcmService.getToken { token ->
            Log.d(TAG, "이 기기의 FCM 토큰: $token")
        }

        autoSaveParkingManager = AutoSaveParkingManager(this)

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

    override val apiClient: ApiClient
        get() = apiClientInstance

    override val authManager: AuthManager
        get() = authManagerInstance

}