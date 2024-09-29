package com.kimnlee.auth.presentation.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.auth.model.LoginRequest
import com.kimnlee.common.auth.model.RegistrationRequest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException

private const val TAG = "LoginViewModel"
class LoginViewModel(private val authManager: AuthManager) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _needsRegistration = MutableStateFlow(false)
    val needsRegistration: StateFlow<Boolean> = _needsRegistration

    private val _registrationResult = MutableStateFlow<Boolean?>(null)
    val registrationResult: StateFlow<Boolean?> = _registrationResult

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private var email: String = ""
    private var picture: String = ""
    private var kakaoAccessToken: String = ""
    private var kakaoRefreshToken: String = ""

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

            if (response.isSuccessful) {
                val authTokenFromHeaders = response.headers()["Authorization"]?.split(" ")?.getOrNull(1)
                authTokenFromHeaders?.let {
                    authManager.saveAuthToken(it)
                }
                val refreshToken = response.headers()["Set-Cookie"]?.let { setCookie ->
                    setCookie.split(";").firstOrNull { it.trimStart().startsWith("refreshToken=") }
                        ?.substringAfter("refreshToken=")
                        ?.trim()
                }
                refreshToken?.let { authManager.saveRefreshToken(it) }
                Log.d(TAG, "sendLoginRequest: Auth token: $authTokenFromHeaders")
                Log.d(TAG, "sendLoginRequest: Refresh token: ${authManager.getAuthToken()}")

                authManager.setLoggedIn(true)
                _isLoggedIn.value = true
            } else {
                Log.e("LoginViewModel", "Login failed: ${response.code()}")
            }
        } catch (e: HttpException) {
            if (e.code() == 404) {
                val errorBody = e.response()?.errorBody()?.string()
                errorBody?.let {
                    val parts = it.split("Email:", "Picture:")
                    if (parts.size >= 3) {
                        email = parts[1].trim().split(",")[0]
                        picture = parts[2].trim()
                    }
                }
                _needsRegistration.value = true
                kakaoAccessToken = token.accessToken
                kakaoRefreshToken = token.refreshToken
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
                picture = picture,
                accessToken = kakaoAccessToken,
                refreshToken = kakaoRefreshToken
            )

            try {
                val response = authManager.register(registrationRequest)

                if (response.isSuccessful) {
                    val authTokenFromHeaders = response.headers()["Authorization"]?.split(" ")?.getOrNull(1)
                    authTokenFromHeaders?.let {
                        authManager.saveAuthToken(it)
                    }
                    val refreshToken = response.headers()["Set-Cookie"]?.let { setCookie ->
                        setCookie.split(";").firstOrNull { it.trimStart().startsWith("refreshToken=") }
                            ?.substringAfter("refreshToken=")
                            ?.trim()
                    }
                    refreshToken?.let { authManager.saveRefreshToken(it) }
                    authManager.setLoggedIn(true)
                    _isLoggedIn.value = true
                    _needsRegistration.value = false
                    _registrationResult.value = true
                    Log.d("KakaoLogin", "로그인 성공 AuthToken: ${authManager.getAuthToken()}, RefreshToken: ${authManager.getRefreshToken()}")
                }
            } catch (e: HttpException) {
                _registrationResult.value = false
                Log.e("LoginViewModel", "Registration failed", e)
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
            }.onFailure { error ->
                // 에러 처리 로직
            }
        }
    }

    fun testLogin() {
        viewModelScope.launch {
            authManager.setLoggedIn(true)
            _isLoggedIn.value = true
            val testToken = "test_auth_token_${System.currentTimeMillis()}"
            authManager.saveAuthToken(testToken)
            Log.i("TestLogin", "Test login successful. Auth Token: $testToken")
        }
    }

    fun testLogout() {
        viewModelScope.launch {
            authManager.setLoggedIn(false)
            _isLoggedIn.value = false
            authManager.clearTokens()
            _registrationResult.value = null
            Log.i("TestLogout", "Test logout successful. Auth Token cleared.")
            _navigationEvent.emit("auth")
        }
    }

    fun resetStatus() {
        viewModelScope.launch {
            authManager.setLoggedIn(false)
            _isLoggedIn.value = false
            authManager.clearTokens()
            _registrationResult.value = null
            _needsRegistration.value = false
        }
    }
}