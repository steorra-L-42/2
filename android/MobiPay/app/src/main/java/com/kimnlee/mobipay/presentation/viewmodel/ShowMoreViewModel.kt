package com.kimnlee.mobipay.presentation.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.common.auth.AuthManager
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
        loadUserInfo()
    }

    private fun loadUserInfo() {
        val userInfo = authManager.getUserInfo()
        _userName.value = userInfo.name
        _userPicture.value = userInfo.picture
        Log.d(TAG, "User info loaded: name=${userInfo.name}, picture=${userInfo.picture}")
    }
}