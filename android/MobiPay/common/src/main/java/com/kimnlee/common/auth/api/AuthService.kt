// common 모듈에 AuthService 인터페이스 정의
package com.kimnlee.common.auth.api

import com.kimnlee.common.auth.model.LoginRequest
import com.kimnlee.common.auth.model.LoginResponse
import com.kimnlee.common.auth.model.RegistrationRequest
import com.kimnlee.common.auth.model.RegistrationResponse
import com.kimnlee.common.auth.model.SendTokenRequest
import com.kimnlee.common.auth.model.SendTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/v1/users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/v1/users/detail")
    suspend fun register(@Body registrationRequest: RegistrationRequest): Response<RegistrationResponse>

    @POST("api/v1/fcm/registertoken")
    suspend fun sendTokens(@Body sendTokenRequest: SendTokenRequest): Response<SendTokenResponse>

    @POST("api/v1/users/logout")
    suspend fun logout(): Response<Void>
}