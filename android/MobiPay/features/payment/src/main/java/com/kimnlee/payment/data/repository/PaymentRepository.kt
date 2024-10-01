package com.kimnlee.payment.data.repository

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.location.Location
import android.net.Uri
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.kimnlee.common.PaymentOperations
import com.kimnlee.payment.data.api.PaymentApiService
import com.kimnlee.payment.data.model.Photos
import com.kimnlee.common.FCMData
import com.kimnlee.common.utils.MobiNotificationManager
import com.kimnlee.payment.data.model.PaymentApprovalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

private const val TAG = "PaymentRepository"
class PaymentRepository(
    private val authenticatedApi: PaymentApiService,
    private val mobiNotificationManager: MobiNotificationManager,
    private val context: Context
) : PaymentOperations {

    private var currentLocation: LatLng? = null
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun getPhotos(): List<Photos> {
        return authenticatedApi.getPhotos().filter { photo -> photo.id <= 5 }
    }

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

        if (listOf(
                fcmData.approvalWaitingId,
                fcmData.merchantId,
                fcmData.paymentBalance,
                fcmData.cardNo,
                fcmData.info
            ).any { it == null }) {
            Log.d(TAG, "processAutoPay: NULL 값이 확인되어 결제 요청을 승인할 수 없습니다.")
            return
        }

        val paymentApprovalData = PaymentApprovalData(
            approvalWaitingId = fcmData.approvalWaitingId!!,
            merchantId = fcmData.merchantId!!,
            paymentBalance = fcmData.paymentBalance!!,
            cardNo = fcmData.cardNo!!,
            info = fcmData.info!!,
            approved = true
        )

        val paymentApprovalDataJson = Gson().toJson(paymentApprovalData)
        // 서버로 Approval 전송
        val call = authenticatedApi.approvePaymentRequest(paymentApprovalDataJson)

        Log.d(TAG, "processAutoPay: 자동결제 준비 완료!")
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 결제 성공
                    Log.d(TAG, "processAutoPay: 결제 승인 요청 성공")
                    // 모바일에 알림 표시

                    val notiTitle = "모비페이 자동결제 완료"
                    val notiMsg = "${fcmData.merchantName}, ${moneyFormat(fcmData.paymentBalance)}"

                    val deepLinkUri = "mobipay://paymentsucceed"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUri)).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra("fcmData", Gson().toJson(fcmData))
                    }

                    val pendingIntent = PendingIntent.getActivity(
                        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    mobiNotificationManager.showNotification(notiTitle, notiMsg, pendingIntent)
                    // 인 앱 알림 목록에 추가

                    // 차량 알림 표시

                } else {
                    // 서버에서 결과는 받았으나 오류 발생
                    Log.d(TAG, "processAutoPay: 결제 승인 요청 실패 - 서버 메세지: ${response.code()} : ${response.message()}")
                    // 모바일에 알림 표시
                    // (결제에 실패했어요. 앱에서 실패 원인을 확인할 수 있어요.)
                    // 인 앱 알림 목록에 추가
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d(TAG, "processAutoPay: 결제 승인 요청 실패 - 네트워크 오류: ${t.localizedMessage}")
                // 네트워크 오류 처리
                // (결제에 실패했어요. 앱에서 실패 원인을 확인할 수 있어요.)
                // 인 앱 알림 목록에 추가
            }
        })

    }

//    fun processAutoPay(fcmData: FCMData) {
//
//        if (listOf(
//                fcmData.approvalWaitingId,
//                fcmData.merchantId,
//                fcmData.paymentBalance,
//                fcmData.cardNo,
//                fcmData.info
//            ).any { it == null }) {
//            Log.d(TAG, "processAutoPay: NULL 값이 확인되어 결제 요청을 승인할 수 없습니다.")
//            return
//        }
//
//        val paymentApprovalData = PaymentApprovalData(
//            approvalWaitingId = fcmData.approvalWaitingId!!,
//            merchantId = fcmData.merchantId!!,
//            paymentBalance = fcmData.paymentBalance!!,
//            cardNo = fcmData.cardNo!!,
//            info = fcmData.info!!,
//            approved = true
//        )
//
//
//    }

    fun moneyFormat(paymentBalance: String?): String {
        if(paymentBalance == null)
            return "오류"

        val number = paymentBalance.toLongOrNull() ?: 0L  // Convert the string to Long, handle null or invalid values
        val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)  // Locale.US to format with commas
        return numberFormat.format(number)
    }

    override fun processFCM(fcmData: FCMData) {
        // null check 후 호출 됨
        val latLng = LatLng(fcmData.lat!!.toDouble(), fcmData.lng!!.toDouble())

        getCurrentLocation { currentLocation ->
            if (currentLocation != null) {
                if (verifyGPS(latLng)) {
                    Log.d(TAG, "processFCM: 성공 100M 이내에 있음")
                    // 자동결제 ON이면
                    if(fcmData.autoPay != null && fcmData.autoPay.equals("true"))
                        processAutoPay(fcmData)
                    else
                        Log.d(TAG, "processFCM: autoPay가 아님")
//                        processManualPay(fcmData)

                    //OFF면
                }else {
                    Log.d(TAG, "processFCM: 실패 100M 밖에 있음")
                }
            } else {
                Log.d(TAG, "processFCM: 현재 위치를 가져오지 못했습니다.")
            }
        }
    }

}