package com.kimnlee.auth.presentation.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.auth.model.LoginRequest
import com.kimnlee.common.auth.model.RegistrationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val authManager: AuthManager) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken

    private val _needsRegistration = MutableStateFlow(false)
    val needsRegistration: StateFlow<Boolean> = _needsRegistration

    private val _registrationResult = MutableStateFlow<Boolean?>(null)
    val registrationResult: StateFlow<Boolean?> = _registrationResult

    var email: String = ""
    var picture: String = ""

    init {
        viewModelScope.launch {
            authManager.isLoggedIn.collect {
                _isLoggedIn.value = it
                if (it) {
                    _authToken.value = authManager.getAuthToken()
                }
            }
        }
    }

    fun login(activity: Activity) {
        viewModelScope.launch {
            authManager.loginWithKakao(activity).onSuccess { token ->
                Log.d("KakaoLogin", "카카오에서 받은 토큰: $token")
                sendLoginRequest(token)
            }.onFailure { error ->
                Log.e("LoginViewModel", "Kakao login failed", error)
            }
        }
    }

    private suspend fun sendLoginRequest(token: OAuthToken) {
        val loginRequest = LoginRequest(
            accessToken = token.accessToken,
            accessTokenExpiresAt = token.accessTokenExpiresAt.toString(),
            refreshToken = token.refreshToken,
            refreshTokenExpiresAt = token.refreshTokenExpiresAt.toString(),
            idToken = token.idToken,
            scopes = listOf("account_email", "profile_image", "talk_message")
        )

        try {
            val response = authManager.login(loginRequest)
            if (response.success) {
                response.authToken?.let { authManager.saveAuthToken(it) }
                response.refreshToken?.let { authManager.saveRefreshToken(it) }
                authManager.setLoggedIn(true)
                _isLoggedIn.value = true
                _authToken.value = response.authToken
            } else {
                _needsRegistration.value = true
                email = "" // 백엔드에서 받아온 이메일
                picture = "" // 백엔드에서 받아온 프로필 사진 URL
            }
        } catch (e: HttpException) {
            if (e.code() == 404) {
                _needsRegistration.value = true
                email = "" // 백엔드에서 받아온 이메일
                picture = "" // 백엔드에서 받아온 프로필 사진 URL
            } else {
                Log.e("LoginViewModel", "Login failed", e)
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Login failed", e)
        }
    }

    fun register(name: String, phoneNumber: String) {
        viewModelScope.launch {
            val registrationRequest = RegistrationRequest(
                email = email,
                name = name,
                phoneNumber = phoneNumber,
                picture = picture
            )

            try {
                val response = authManager.register(registrationRequest)
                if (response.success) {
                    response.authToken?.let { authManager.saveAuthToken(it) }
                    response.refreshToken?.let { authManager.saveRefreshToken(it) }
                    authManager.setLoggedIn(true)
                    _isLoggedIn.value = true
                    _authToken.value = response.authToken
                    _needsRegistration.value = false
                    _registrationResult.value = true
                } else {
                    Log.e("LoginViewModel", "Registration failed: ${response.message}")
                    _registrationResult.value = false
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Registration failed", e)
                _registrationResult.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authManager.logout().onSuccess {
                authManager.setLoggedIn(false)
                _isLoggedIn.value = false
                _authToken.value = null
            }.onFailure { error ->
                // 에러 처리 로직
            }
        }
    }

    // 임시 로그인
    fun testLogin() {
        viewModelScope.launch {
            authManager.setLoggedIn(true)
            _isLoggedIn.value = true
            val testToken = "test_auth_token_${System.currentTimeMillis()}"
            authManager.saveAuthToken(testToken)
            _authToken.value = testToken
            Log.i("TestLogin", "Test login successful. Auth Token: $testToken")
        }
    }

    // 임시 로그아웃
    fun testLogout() {
        viewModelScope.launch {
            authManager.setLoggedIn(false)
            _isLoggedIn.value = false
            authManager.clearTokens()
            _authToken.value = null
            Log.i("TestLogout", "Test logout successful. Auth Token cleared.")
        }
    }
}