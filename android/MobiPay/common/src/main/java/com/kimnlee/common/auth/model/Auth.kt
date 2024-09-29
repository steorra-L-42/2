package com.kimnlee.common.auth.model

data class LoginRequest(
    val accessToken: String,
    val accessTokenExpiresAt: String,
    val refreshToken: String,
    val refreshTokenExpiresAt: String,
    val idToken: String?,
    val scopes: List<String>
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val authToken: String?,
    val refreshToken: String?
)

data class RegistrationRequest(
    val email: String,
    val name: String,
    val phoneNumber: String,
    val picture: String
)

data class RegistrationResponse(
    val success: Boolean,
    val message: String,
    val authToken: String?,
    val refreshToken: String?
)