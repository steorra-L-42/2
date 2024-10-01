package com.kimnlee.common

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
}

interface FCMDependencyProvider : ApiClientProvider, AuthManagerProvider {
    val paymentOperations: PaymentOperations
}