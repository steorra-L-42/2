package com.kimnlee.common.auth.repository

import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.auth.api.AuthService
import com.kimnlee.common.auth.model.LoginRequest
import com.kimnlee.common.auth.model.RegistrationRequest
import com.kimnlee.common.network.ApiClient
import retrofit2.Response

class ApiAuthService(authManager: AuthManager) : AuthService {

    private val unAuthenticatedApi: AuthService = ApiClient.getInstance(authManager)
        .unAuthenticatedApi
        .create(AuthService::class.java)

    override suspend fun login(loginRequest: LoginRequest): Response<Void> {
        return unAuthenticatedApi.login(loginRequest)
    }

    override suspend fun register(registrationRequest: RegistrationRequest): Response<Void> {
        return unAuthenticatedApi.register(registrationRequest)
    }
}