package com.kimnlee.common.auth.repository

import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.auth.api.UnAuthService
import com.kimnlee.common.auth.model.LoginRequest
import com.kimnlee.common.auth.model.LoginResponse
import com.kimnlee.common.auth.model.RegistrationRequest
import com.kimnlee.common.auth.model.RegistrationResponse
import com.kimnlee.common.network.ApiClient

class ApiUnAuthService(authManager: AuthManager) : UnAuthService {

    private val unAuthenticatedApi: UnAuthService = ApiClient.getInstance(authManager)
        .unAuthenticatedApi
        .create(UnAuthService::class.java)

    override suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return unAuthenticatedApi.login(loginRequest)
    }

    override suspend fun register(registrationRequest: RegistrationRequest): RegistrationResponse {
        return unAuthenticatedApi.register(registrationRequest)
    }
}