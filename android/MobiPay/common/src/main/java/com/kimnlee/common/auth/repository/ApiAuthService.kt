package com.kimnlee.common.auth.repository

import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.auth.api.AuthService
import com.kimnlee.common.auth.model.LoginRequest
import com.kimnlee.common.auth.model.RegistrationRequest
import com.kimnlee.common.auth.model.SendTokenRequest
import com.kimnlee.common.auth.model.SendTokenResponse
import com.kimnlee.common.network.ApiClient
import retrofit2.Response

class ApiAuthService(
    private val unAuthenticatedApi: AuthService,
    private val authenticatedApi: AuthService
) : AuthService {

    override suspend fun login(loginRequest: LoginRequest): Response<Void> {
        return unAuthenticatedApi.login(loginRequest)
    }

    override suspend fun register(registrationRequest: RegistrationRequest): Response<Void> {
        return unAuthenticatedApi.register(registrationRequest)
    }

    override suspend fun sendTokens(sendTokenRequest: SendTokenRequest): Response<SendTokenResponse> {
        return unAuthenticatedApi.sendTokens(sendTokenRequest)
    }

    override suspend fun logout(): Response<Void> {
        return authenticatedApi.logout()
    }

    companion object {
        fun create(apiClient: ApiClient): ApiAuthService {
            val unAuthenticatedApi = apiClient.unAuthenticatedApi.create(AuthService::class.java)
            val authenticatedApi = apiClient.authenticatedApi.create(AuthService::class.java)
            return ApiAuthService(unAuthenticatedApi, authenticatedApi)
        }
    }
}