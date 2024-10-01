package com.kimnlee.common

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
    fun processFCM(lat: String, lng: String)
}

interface FCMDependencyProvider : ApiClientProvider, AuthManagerProvider {
    val paymentOperations: PaymentOperations
}