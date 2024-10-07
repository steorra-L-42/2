package com.kimnlee.payment.data.repository

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.navigation.NavDeepLinkBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.kimnlee.common.FCMData
import com.kimnlee.common.PaymentOperations
import com.kimnlee.common.utils.AAFocusManager
import com.kimnlee.common.utils.MobiNotificationManager
import com.kimnlee.common.utils.moneyFormat
import com.kimnlee.payment.data.api.PaymentApiService
import com.kimnlee.payment.data.model.PaymentApprovalData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

private const val TAG = "PaymentRepository"
class PaymentRepository(
    private val authenticatedApi: PaymentApiService,
    private val mobiNotificationManager: MobiNotificationManager,
    private val context: Context
) : PaymentOperations {

    private val _fcmDataToNavigate = MutableStateFlow<FCMData?>(null)
    val fcmDataToNavigate: StateFlow<FCMData?> get() = _fcmDataToNavigate


    private var currentLocation: LatLng? = null
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun getCurrentLocation(onLocationReceived: (LatLng?) -> Unit) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = LatLng(location.latitude, location.longitude)
                    onLocationReceived(currentLocation)
                } else {
                    onLocationReceived(null)
                    Log.d(TAG, "현재 위치 NULL")
                }
            }.addOnFailureListener {
                Log.e(TAG, "현재 위치 가져오기 실패", it)
                onLocationReceived(null)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "권한 없음", e)
            onLocationReceived(null)
        }
    }

    override fun verifyGPS(latlng: LatLng): Boolean {
        currentLocation?.let { currentLatLng ->
            val distance = FloatArray(1)
            Location.distanceBetween(
                currentLatLng.latitude,
                currentLatLng.longitude,
                latlng.latitude,
                latlng.longitude,
                distance
            )
            return distance[0] <= 100
        }
        return false
    }

    private fun processAutoPay(fcmData: FCMData) {
        Log.d(TAG, "processAutoPay: 자동결제 처리")

        processPay(fcmData, true)

    }

    fun approveManualPay(fcmData: FCMData, cardNo: String){
        val newFcmData = FCMData(
            fcmData.autoPay,
            cardNo,
            fcmData.approvalWaitingId,
            fcmData.merchantId,
            fcmData.paymentBalance,
            fcmData.merchantName,
            fcmData.info,
            fcmData.lat,
            fcmData.lng,
            fcmData.type
        )
        processPay(newFcmData, false)
    }

    override fun processPay(fcmData: FCMData, isAutoPay: Boolean){

        val approvalWaitingId = fcmData.approvalWaitingId?.toLongOrNull()
        val merchantId = fcmData.merchantId?.toLongOrNull()
        val paymentBalance = fcmData.paymentBalance?.toLongOrNull()

        if (listOf(
                fcmData.approvalWaitingId,
                fcmData.merchantId,
                fcmData.paymentBalance,
                fcmData.cardNo,
                fcmData.info
            ).any { it == null }) {
            Log.d(TAG, "processPay: NULL 값이 확인되어 결제 요청을 승인할 수 없습니다.")
            if(fcmData != null){
                Log.d(TAG, "processPay: $fcmData")
            }
            return
        }

        val paymentApprovalData = PaymentApprovalData(
            approvalWaitingId = approvalWaitingId!!,
            merchantId = merchantId!!,
            paymentBalance = paymentBalance!!,
            cardNo = fcmData.cardNo!!,
            info = fcmData.info!!,
            approved = true
        )

        Log.d(TAG, "processPay: 서버에 결제요청. 이 정보로: ${paymentApprovalData.toString()}")
        val paymentApprovalDataJson = Gson().toJson(paymentApprovalData)
        // 서버로 Approval 전송
        val call = authenticatedApi.approvePaymentRequest(paymentApprovalData)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

                if (response.isSuccessful) { // 결제 성공
//                if (response.isSuccessful || 1 == 1) { // 임시 코드
//                    Log.e(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
//                    Log.e(TAG, "@@@        임시 코드 사용중        @@")
//                    Log.e(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")

                    Log.d(TAG, "processPay: 결제 성공")
                    // 모바일에 알림 표시


                    val deepLinkUri = "mobipay://paymentsucceed"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUri)).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra("fcmData", Gson().toJson(fcmData))
                    }

                    val pendingIntent = PendingIntent.getActivity(
                        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val amountKRW = moneyFormat(fcmData.paymentBalance!!.toBigInteger())

                    var notiTitle = if (isAutoPay) "모비페이 자동결제 완료" else "모비페이 결제완료"

                    var notiMsg = "${fcmData.merchantName}\n${amountKRW}원 결제완료"

                    // TODO 인 앱 알림목록에 추가

                    // 차량 알림 표시
                    if (AAFocusManager.isAAConnected && AAFocusManager.isAppInFocus && isAutoPay) {
                        // Alert
                        if (isAutoPay)
                            notiTitle = "모비페이 자동결제"

                        mobiNotificationManager.broadcastForPlainAlert(notiTitle, notiMsg)

                    } else if (AAFocusManager.isAAConnected) {
                        // HUN
                        mobiNotificationManager.broadcastForPlainHUN(notiTitle, "${fcmData.merchantName}  ${amountKRW}원")
                    }
                    // 휴대폰은 무조건
                    mobiNotificationManager.showNotification(notiTitle, notiMsg, pendingIntent)

                } else {
                    // 서버에서 결과는 받았으나 오류 발생
                    Log.d(TAG, "processPay: 결제 승인 요청 실패 - 서버 메세지: ${response.code()} : ${response.message()}")

                    // TODO 인 앱 알림목록에 추가

                    // 모바일에 알림 표시
                    mobiNotificationManager.showPlainNotification("모비페이 결제 실패", "결제에 실패했어요.\n앱에서 자세한 내용을 확인할 수 있어요.")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d(TAG, "processPay: 결제 승인 요청 실패 - 네트워크 오류: ${t.localizedMessage}")
                // 네트워크 오류 처리
                mobiNotificationManager.showPlainNotification("모비페이 결제 실패", "네트워크 오류로 결제에 실패했어요.\n다른 결제수단으로 직접 결제해주세요.")
                // TODO 인 앱 알림 목록에 추가
            }
        })
    }


    private fun processManualPay(fcmData: FCMData) {
        Log.d(TAG, "processManualPay: 수동결제 처리")

        if (listOf(
                fcmData.approvalWaitingId,
                fcmData.merchantId,
                fcmData.paymentBalance,
                fcmData.info
            ).any { it == null }) {
            Log.d(TAG, "processManualPay: NULL 값이 확인되어 결제 요청을 승인할 수 없습니다. NULL인 새키 ${fcmData.toString()}")
            return
        }

        if (AAFocusManager.isAAConnected && AAFocusManager.isAppInFocus) {
            // Alert
            Log.d(TAG, "processManualPay: Alert 요청함 manual_pay로")
            mobiNotificationManager.broadcastForAlert("manual_pay", fcmData)
        } else if (AAFocusManager.isAAConnected) {
            // HUN
            Log.d(TAG, "processManualPay: HUN 요청함 manual_pay로")
            mobiNotificationManager.broadcastForHUN("manual_pay", fcmData)
        }else{
            // 폰
            // TODO 휴대폰으로 결제 요청하는 로직 작성 필요
//            _fcmDataToNavigate.value = fcmData
            // mobiNotificationManager.showNotification() ??????
            val fcmDataJson = Uri.encode(Gson().toJson(fcmData))
            val deepLinkUri = Uri.parse("mobipay://payment_requestmanualpay?fcmData=$fcmDataJson")

            val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context, Random.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val amountKRW = moneyFormat(fcmData.paymentBalance!!.toBigInteger())
            val notiTitle = "모비페이 결제 요청"
            val notiMsg = "${fcmData.merchantName}에서\n${amountKRW} 결제를 요청했어요!"

            mobiNotificationManager.showNotification(notiTitle, notiMsg, pendingIntent)

        }

    }

    override fun processFCM(fcmData: FCMData) {
        // null check 후 호출 됨
        val latLng = LatLng(fcmData.lat!!.toDouble(), fcmData.lng!!.toDouble())

        getCurrentLocation { currentLocation ->
            if (currentLocation != null) {
                if (verifyGPS(latLng)) {
                    Log.d(TAG, "processFCM: 성공 100M 이내에 있음")

                    if(fcmData.autoPay != null && fcmData.autoPay.equals("true")) { //자동결제
                        processAutoPay(fcmData)
                    }else { // 수동결제
                        Log.d(TAG, "processFCM: autoPay가 아님")
                        processManualPay(fcmData)
                    }
                }else {
                    Log.d(TAG, "processFCM: 실패 100M 밖에 있음")
                    if(fcmData.autoPay != null && fcmData.autoPay.equals("true")) {
                        mobiNotificationManager.showPlainNotification("모비페이 자동결제 실패", "결제를 요청한 가맹점 근처(100m 이내)에\n있을 때에만 자동결제가 가능해요.")
                    }
                }
            } else {
                Log.d(TAG, "processFCM: 현재 위치를 가져오지 못했습니다.")
            }
        }
    }

}