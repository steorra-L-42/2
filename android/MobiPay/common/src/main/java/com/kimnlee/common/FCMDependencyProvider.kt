package com.kimnlee.common

import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.network.ApiClient

interface ApiClientProvider {
    val apiClient: ApiClient
}

interface AuthManagerProvider {
    val authManager: AuthManager
}

// 여기에 PaymentRepository 메서드를 적으면 사용 가능
interface PaymentOperations {
    fun verifyGPS(latlng: LatLng): Boolean
    fun processFCM(fcmData: FCMData)
    fun processPay(fcmData: FCMData, isAutoPay: Boolean)
}

interface MemberInvitationOperations{
    fun sendInvitation(phoneNumber:String, vehicleId: Int)
    fun processFCM(fcmDataForInvitation: FCMDataForInvitation)
    fun acceptInvitation(invitationId: Int)
}

interface FCMDependencyProvider : ApiClientProvider, AuthManagerProvider {
    val paymentOperations: PaymentOperations
    val memberInvitationOperations: MemberInvitationOperations
}