package com.kimnlee.common.auth.model

data class LoginRequest(
    val accessToken: String,
    val accessTokenExpiresAt: String,
    val refreshToken: String,
    val refreshTokenExpiresAt: String,
    val idToken: String?,
    val scopes: List<String>
)

data class RegistrationRequest(
    val email: String,
    val name: String,
    val phoneNumber: String,
    val picture: String,
    val accessToken: String,
    val refreshToken: String,
)

data class SendTokenRequest(
    val token: String
)

data class SendTokenResponse(
    val message: String
)