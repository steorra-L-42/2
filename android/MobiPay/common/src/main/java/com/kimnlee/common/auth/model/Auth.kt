package com.kimnlee.common.auth.model

data class LoginRequest(
    val accessToken: String,
    val accessTokenExpiresAt: String,
    val refreshToken: String,
    val refreshTokenExpiresAt: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val authToken: String?,
    val refreshToken: String?
)