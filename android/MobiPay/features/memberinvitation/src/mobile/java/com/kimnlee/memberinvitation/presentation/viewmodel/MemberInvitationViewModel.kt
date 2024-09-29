package com.kimnlee.memberinvitation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MemberInvitationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _showInvitationBLE = MutableStateFlow(false)
    val showInvitationBLE: StateFlow<Boolean> = _showInvitationBLE.asStateFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()


    fun updatePhoneNumber(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }
    fun openBottomSheet() {
        _showBottomSheet.value = true
    }

    fun closeBottomSheet() {
        _showBottomSheet.value = false
    }

    fun openInvitationBLE(){
        _showInvitationBLE.value = true
    }
    fun closeInvitationBLE(){
        _showInvitationBLE.value = false
    }
    fun inviteMember() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = checkUserExistence(_phoneNumber.value)
            if (result) {
                sendInvitation(_phoneNumber.value)
                _uiState.value = UiState.InvitationSent
            } else {
                _uiState.value = UiState.UserNotFound
            }
        }
    }

    private suspend fun checkUserExistence(phoneNumber: String): Boolean {
        // 가입된 유저인지 확인
        return true
    }

    private suspend fun sendInvitation(phoneNumber: String) {
        // 초대 알림 전송
    }

    sealed class UiState {
        object Initial : UiState()
        object Loading : UiState()
        object InvitationSent : UiState()
        object UserNotFound : UiState()
    }
}