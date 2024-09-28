package com.kimnlee.auth.presentation.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.auth.model.LoginRequest
import com.kimnlee.common.auth.model.RegistrationRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
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

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    var email: String = ""
    var picture: String = ""

    init {
        viewModelScope.launch {
            combine(isLoggedIn, needsRegistration) { isLoggedIn, needsRegistration ->
                when {
                    isLoggedIn -> "home"
                    needsRegistration -> "registration"
                    else -> "auth"
                }
            }.collect { route ->
                _navigationEvent.emit(route)
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
            } else if (e.code() == 500) {
                // 임시 테스트
                _needsRegistration.value = true
                email = "test@email.com" // 백엔드에서 받아온 이메일
                picture = "test_image" // 백엔드에서 받아온 프로필 사진 URL
                Log.d("KakaoLogin", "500에러 sendLoginRequest에서 받음")
                Log.d("KakaoLogin", "needsRegistration 값: ${needsRegistration.value}")
            } else {
                Log.e("LoginViewModel", "Login failed", e)
            }
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
            } catch (e: HttpException) {
                if (e.code() == 500) {
                    Log.e("LoginViewModel", "Registration failed", e)
                    _registrationResult.value = true
                    // 임시로 로그인 된척 하기(테스트)
                    _needsRegistration.value = false
                    authManager.setLoggedIn(true)
                    _isLoggedIn.value = true
                    val testToken = "test_auth_token_${System.currentTimeMillis()}"
                    authManager.saveAuthToken(testToken)
                    _authToken.value = testToken
                    Log.i("TestLogin", "Test login successful. Auth Token: $testToken")
                }
            }

            // 네비게이션 이벤트는 모든 처리가 끝난 후 한 번만 발생시킴
            if (_isLoggedIn.value) {
                _navigationEvent.emit("home")
                Log.d("LoginViewModel", "Emitted navigation event to home")
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
            _registrationResult.value = null
            Log.i("TestLogout", "Test logout successful. Auth Token cleared.")
            _navigationEvent.emit("auth")
        }
    }

    // 로그인 중에 뒤로가기 버튼을 누르면 초기화면으로 돌아오면서 상태들 초기화
    fun resetStatus() {
        viewModelScope.launch {
            authManager.setLoggedIn(false)
            _isLoggedIn.value = false
            authManager.clearTokens()
            _authToken.value = null
            _registrationResult.value = null
            _needsRegistration.value = false
        }
    }
}