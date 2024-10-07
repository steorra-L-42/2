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

    private val _userPhoneNumber = MutableStateFlow<String>("")
    val userPhoneNumber: StateFlow<String> = _userPhoneNumber.asStateFlow()

    private val _userEmail = MutableStateFlow<String>("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        val userInfo = authManager.getUserInfo()
        _userName.value = userInfo.name
        _userPicture.value = userInfo.picture
        _userPhoneNumber.value = userInfo.phoneNumber
        _userEmail.value = userInfo.email
        Log.d(TAG, "User info loaded: name=${userInfo.name}, picture=${userInfo.picture}, phoneNumber=${userInfo.phoneNumber}, email=${userInfo.email}")
    }
}