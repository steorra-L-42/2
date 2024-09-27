package com.kimnlee.auth.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.common.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authManager: AuthManager) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken

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

    fun login() {
        viewModelScope.launch {
            authManager.loginWithKakao().onSuccess {
                authManager.setLoggedIn(true)
                _isLoggedIn.value = true
                _authToken.value = authManager.getAuthToken()
                // 지금 임시로 authManager에서 카카오 accessToken 저장시켜 놓음
                // 나중에 백엔드 서버로 보내서 우리 서버 authToken 받을 예정
                Log.i("KakaoLogin", "발급된 카카오 accessToken : ${authToken.value}")
            }.onFailure { error ->
                // 에러 처리 로직
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
            authManager.clearAuthToken()
            _authToken.value = null
            Log.i("TestLogout", "Test logout successful. Auth Token cleared.")
        }
    }
}