package com.kimnlee.firebase

import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.kimnlee.common.FCMData
import com.kimnlee.common.FCMDataForInvitation
import com.kimnlee.common.FCMDependencyProvider
import com.kimnlee.common.MemberInvitationOperations
import com.kimnlee.common.PaymentOperations
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.network.ApiClient
import com.kimnlee.common.network.BackendService
import com.kimnlee.common.utils.AAFocusManager
import com.kimnlee.common.utils.MobiNotificationManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

private const val TAG = "FCMService"
private const val FCM_TYPE_MEMBER_INVITATION = "invitation"
private const val FCM_TYPE_PAYMENT_REQUEST = "transactionRequest"
private const val FCM_TYPE_PAYMENT_RESULT = "transactionResult"
private const val FCM_TYPE_AUTO_PAY_FAILURE = "autoPayFailed"

class FCMService : FirebaseMessagingService() {

    private var apiClient: ApiClient? = null
    private var authManager: AuthManager? = null
    private var paymentOperations: PaymentOperations? = null
    private var memberInvitationOperations: MemberInvitationOperations? = null
    private lateinit var mNotificationManager: NotificationManagerCompat

    private lateinit var notificationManager: MobiNotificationManager

    private val fcmApi: Retrofit? by lazy {
        apiClient?.fcmApi
    }
    private val backendService: BackendService? by lazy {
        fcmApi?.create(BackendService::class.java)
    }

    // Service는 인자로 전달하지 못한다고 해서 common 모듈에 FCMDependencyProvider 만들고
    // MobipayApplication에서 초기화된 apiClient, authManager, paymentRepository 인스턴스 가져오기
    private fun initializeDependencies() {
        val dependencyProvider = applicationContext as? FCMDependencyProvider
        if (dependencyProvider == null) {
            Log.e(TAG, "Application context does not implement FCMDependencyProvider")
            return
        }

        apiClient = dependencyProvider.apiClient
        authManager = dependencyProvider.authManager
        paymentOperations = dependencyProvider.paymentOperations
        memberInvitationOperations = dependencyProvider.memberInvitationOperations

        mNotificationManager = NotificationManagerCompat.from(applicationContext)

        notificationManager = MobiNotificationManager.getInstance(applicationContext)

    }

    fun getToken(callback: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM Token: $token")
                callback(token)
            } else {
                Log.w(TAG, "Fetching FCM token failed", task.exception)
                callback("Failed")
            }
        }
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate: FCM onCreate")

        initializeDependencies()

        // FCM 토큰 확인
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM Token: $token")
                sendTokenToServer(token) // 서버에 전송
            } else {
                Log.w(TAG, "Fetching FCM token failed", task.exception)
            }
        }

        Log.d(TAG, "onCreate: BASE URL = ${fcmApi?.baseUrl()}")

    }

    fun processMessage(remoteMessage: RemoteMessage){
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "페이로드: ${remoteMessage.data}")

            Log.d(TAG, "processMessage: 안드로이드 오토 화면 켜져있는지 = ${AAFocusManager.isAppInFocus}")

            val responseJsonString = Gson().toJson(remoteMessage.data)

            val fcmData = Gson().fromJson(responseJsonString, FCMData::class.java)

            when (fcmData.type) {
                FCM_TYPE_PAYMENT_RESULT -> {
                    // 결제 결과화면 표시
                }
                FCM_TYPE_PAYMENT_REQUEST -> {
                    if (fcmData.lat != null && fcmData.lng != null) {
                        paymentOperations?.processFCM(fcmData)
                    }
                }
                FCM_TYPE_MEMBER_INVITATION -> {
                    val rjs = Gson().toJson(remoteMessage.data)
                    val fcmDataForInvitation = Gson().fromJson(rjs, FCMDataForInvitation::class.java)
                    memberInvitationOperations?.processFCM(fcmDataForInvitation)
                }
                FCM_TYPE_AUTO_PAY_FAILURE -> {

                }
                else -> {
//                    Log.d(TAG, "processMessage: 이것은 ELSE에 속한다.")
                    try{
                        val rjs = Gson().toJson(remoteMessage.data)
                        val fcmDataForInvitation = Gson().fromJson(rjs, FCMDataForInvitation::class.java)

                        if (fcmDataForInvitation.title?.contains("초대") == true){
                            memberInvitationOperations?.processFCM(fcmDataForInvitation)
                        }

                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }

        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            processMessage(remoteMessage)
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Token", "새 토큰: " + token)
        // 백엔드 서버에 FCM 토큰 전송
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        val call = backendService?.registerToken(token)
        call?.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "onResponse: FCM 토큰 정상 등록됨")
                } else {
                    Log.d(TAG, "onResponse: FCM 토큰 등록 실패")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d(TAG, "onFailure: FCM 토큰 서버 통신 오류 \n${Log.getStackTraceString(t)}")
            }
        })
    }
}
