package com.kimnlee.memberinvitation.data.repository

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.net.Uri
import com.google.gson.Gson
import com.kimnlee.common.FCMDataForInvitation
import com.kimnlee.common.FCMDependencyProvider
import com.kimnlee.common.MemberInvitationOperations
import com.kimnlee.common.utils.MobiNotificationManager
import com.kimnlee.common.utils.isAppInForeground
import com.kimnlee.memberinvitation.data.api.MemberInvitationApiService
import com.kimnlee.memberinvitation.data.model.MemberInvitationAcceptData
import com.kimnlee.memberinvitation.data.model.MemberInvitationData
import com.kimnlee.memberinvitation.data.model.MemberInvitationResponse
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

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
                        mobiNotificationManager.showPlainNotification("멤버 초대 성공", "멤버 초대요청에 성공했어요!\n초대받은 회원이 수락하면 멤버에 추가돼요.")

                    } else {
                        // 서버에서 결과는 받았으나 오류 발생
                        Log.d(TAG, "sendInvitation: 초대 요청 실패 - 서버 메세지: ${response.code()} : ${response.message()}")
                        // TODO 인 앱 알림목록에 추가
                        // 모바일에 알림 표시
                        mobiNotificationManager.showPlainNotification("멤버 초대 실패", "멤버 초대에 실패했어요.\n모비페이에 가입된 회원이 아니에요.")
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

    fun sendInvitationPhone(phoneNumber:String, vehicleId: Int, onResult: () -> Unit){

        val apiClient = (context.applicationContext as? FCMDependencyProvider)?.apiClient
        if(apiClient != null){
            val call = authenticatedApi.invitationRequest(MemberInvitationData(phoneNumber, vehicleId))
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {

                    if (response.isSuccessful) { // 초대요청 성공
                        Log.d(TAG, "sendInvitation onResponse: 초대요청 성공")
                        mobiNotificationManager.showPlainNotification("멤버 초대 성공", "멤버 초대요청에 성공했어요!\n초대받은 회원이 수락하면 멤버에 추가돼요.")

                    } else {
                        // 서버에서 결과는 받았으나 오류 발생
                        Log.d(TAG, "sendInvitation: 초대 요청 실패 - 서버 메세지: ${response.code()} : ${response.message()}")
                        // TODO 인 앱 알림목록에 추가
                        // 모바일에 알림 표시
                        mobiNotificationManager.showPlainNotification("멤버 초대 실패", "멤버 초대에 실패했어요.\n모비페이에 가입된 회원이 아니에요.")
                    }
                    onResult()

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

    override fun acceptInvitation(invitationId: Int){

        val apiClient = (context.applicationContext as? FCMDependencyProvider)?.apiClient
        if(apiClient != null){
            val call = authenticatedApi.acceptInvitation(invitationId, MemberInvitationAcceptData("APPROVED"))
            call.enqueue(object : Callback<MemberInvitationResponse> {
                override fun onResponse(
                    call: Call<MemberInvitationResponse>,
                    response: Response<MemberInvitationResponse>
                ) {

                    if (response.isSuccessful) { // 초대요청 성공
                        Log.d(TAG, "acceptInvitation onResponse: 초대 수락 성공")
                        Log.d(TAG, "onResponse: ${response.code()} / ${response.message()} / ${response.message()}")

                    } else {
                        // 서버에서 결과는 받았으나 오류 발생
                        Log.d(TAG, "acceptInvitation: 초대 수락 실패 - 서버 메세지: ${response.code()} : ${response.message()}")

                        // TODO 인 앱 알림목록에 추가

                        // 모바일에 알림 표시
                        mobiNotificationManager.showPlainNotification("초대 요청 성공", "초대를 수락하여 차량이 추가되었어요.")
                    }
                }

                override fun onFailure(call: Call<MemberInvitationResponse>, t: Throwable) {
                    Log.d(TAG, "acceptInvitation: 초대 요청 실패 - 네트워크 오류: ${t.localizedMessage}")
                    // 네트워크 오류 처리
                    mobiNotificationManager.showPlainNotification("초대 수락 실패", "네트워크 오류로 초대 수락에 실패했어요.")
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

        if(isAppInForeground){
            invitationViewModel.handleInvitation(fcmDataForInvitation)
        }else{
//            val deepLinkUri = "mobipay://youvegotinvited"
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUri)).apply {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                putExtra("fcmDataForInvitation", Gson().toJson(fcmDataForInvitation))
//            }
//
//            val pendingIntent = PendingIntent.getActivity(
//                context, Random.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
            val fcmDataForInvitationJson = Uri.encode(Gson().toJson(fcmDataForInvitation))
            Log.d(TAG, "멤버 초대 processFCM: $fcmDataForInvitationJson")
            val deepLinkUri = Uri.parse("mobipay://youvegotinvited?fcmDataForInvitation=$fcmDataForInvitationJson")

            val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context, Random.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )


            val lpNo = fcmDataForInvitation.carNumber
            val prettyLpNo = "${lpNo!!.substring(0, lpNo.length-4)} ${lpNo.substring(lpNo.length-4)}"
            mobiNotificationManager.showNotification("모비페이 멤버 초대", "$prettyLpNo 차량의 멤버로 초대되었어요!", pendingIntent)
        }
//        invitationViewModel.stopAdvertising()


//
//        pendingIntent.send()

    }

    private fun isAnyFieldNull(fcmData: FCMDataForInvitation): Boolean {
        return  fcmData.type == null ||
                fcmData.title == null ||
                fcmData.body == null ||
                fcmData.invitationId == null ||
                fcmData.created == null ||
                fcmData.inviterName == null ||
                fcmData.inviterPicture == null ||
                fcmData.carNumber == null ||
                fcmData.carModel == null
    }

}