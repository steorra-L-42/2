package com.kimnlee.memberinvitation.data.repository

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.navigation.navDeepLink
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.kimnlee.common.FCMData
import com.kimnlee.common.FCMDataForInvitation
import com.kimnlee.common.FCMDependencyProvider
import com.kimnlee.common.MemberInvitationOperations
import com.kimnlee.common.PaymentOperations
import com.kimnlee.common.utils.AAFocusManager
import com.kimnlee.common.utils.MobiNotificationManager
import com.kimnlee.memberinvitation.data.api.MemberInvitationApiService
import com.kimnlee.memberinvitation.data.model.MemberInvitationData
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

private const val TAG = "MemberInvitationReposit"
class MemberInvitationRepository(
    private val authenticatedApi: MemberInvitationApiService,
    private val mobiNotificationManager: MobiNotificationManager,
    private val context: Context,
    private val invitationViewModel: MemberInvitationViewModel
) : MemberInvitationOperations {

    override fun sendInvitation(phoneNumber:String, vehicleId: Int){

        val apiClient = (context.applicationContext as? FCMDependencyProvider)?.apiClient
        if(apiClient != null){
            val call = authenticatedApi.invitationRequest(MemberInvitationData(phoneNumber, vehicleId))
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {

                    if (response.isSuccessful) { // 초대요청 성공
                        Log.d(TAG, "sendInvitation onResponse: 초대요청 성공")

                    } else {
                        // 서버에서 결과는 받았으나 오류 발생
                        Log.d(TAG, "sendInvitation: 초대 요청 실패 - 서버 메세지: ${response.code()} : ${response.message()}")

                        // TODO 인 앱 알림목록에 추가

                        // 모바일에 알림 표시
                        mobiNotificationManager.showPlainNotification("초대 요청 성공", "멤버 초대에 성공했어요.")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d(TAG, "sendInvitation: 초대 요청 실패 - 네트워크 오류: ${t.localizedMessage}")
                    // 네트워크 오류 처리
                    mobiNotificationManager.showPlainNotification("초대 요청 실패", "네트워크 오류로 멤버 초대에 실패했어요.")
                    // TODO 인 앱 알림 목록에 추가
                }
            })
        }


    }


    override fun processFCM(fcmDataForInvitation: FCMDataForInvitation) {

        if (isAnyFieldNull(fcmDataForInvitation)) {
            Log.e(TAG, "멤버 초대 processFCM: NULL값이 발견되어 종료 ${fcmDataForInvitation}")
            // Handle null case here (e.g., return early or notify the user)
            return
        }

//        navController.popBackStack()
//        Log.d(TAG, "멤버 초대 processFCM: DeepLink 가동!")

        Log.d(TAG, "멤버 초대 processFCM: Stop Advertise!")

        invitationViewModel.handleInvitation(fcmDataForInvitation)
//        invitationViewModel.stopAdvertising()

//        val deepLinkUri = "mobipay://youvegotinvited"
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUri)).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            putExtra("fcmDataForInvitation", Gson().toJson(fcmDataForInvitation))
//        }
//
//        val pendingIntent = PendingIntent.getActivity(
//            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        pendingIntent.send()

    }

    private fun isAnyFieldNull(fcmData: FCMDataForInvitation): Boolean {
        return  fcmData.title == null ||
                fcmData.body == null ||
                fcmData.invitationId == null ||
                fcmData.created == null ||
                fcmData.inviterName == null ||
                fcmData.inviterPicture == null ||
                fcmData.carNumber == null ||
//                fcmData.type == null ||
                fcmData.carModel == null
    }

}