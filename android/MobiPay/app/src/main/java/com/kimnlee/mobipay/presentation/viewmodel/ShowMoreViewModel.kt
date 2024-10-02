package com.kimnlee.mobipay.presentation.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.common.auth.AuthManager
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

class ShowMoreViewModel(private val authManager: AuthManager) : ViewModel() {
    private val _userName = MutableStateFlow<String>("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPicture = MutableStateFlow<String>("")
    val userPicture: StateFlow<String> = _userPicture.asStateFlow()

    init {
        viewModelScope.launch {
            loadUserInfo()
        }
    }

    private fun loadUserInfo() {
        val token = authManager.getAuthToken()
        token?.let {
            try {
                val secretKey = authManager.getSecretKey()
                val key = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))

                val claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .payload

                _userName.value = claims["name"] as? String ?: ""
                _userPicture.value = claims["picture"] as? String ?: ""
            } catch (e: Exception) {
                // 토큰 파싱 실패 처리
                Log.i(TAG, "jwt토큰 파싱 실패")
                e.printStackTrace()
            }
        }
    }
}