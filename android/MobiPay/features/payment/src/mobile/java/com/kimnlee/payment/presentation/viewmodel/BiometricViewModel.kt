package com.kimnlee.payment.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val TAG = "BiometricViewModel"
class BiometricViewModel(application: Application) : AndroidViewModel(application) {
    private val _authenticationState =
        MutableStateFlow<AuthenticationState>(AuthenticationState.Idle)
    val authenticationState: StateFlow<AuthenticationState> = _authenticationState

    private val _navigateToPaymentDetail = MutableSharedFlow<Unit>()
    val navigateToPaymentDetail: SharedFlow<Unit> = _navigateToPaymentDetail

    val BIO_AUTH = 1

    fun updateAuthenticationState(state: AuthenticationState) {
        _authenticationState.value = state
        if (state is AuthenticationState.Success) {
            viewModelScope.launch {
                _navigateToPaymentDetail.emit(Unit)
            }
        }
        Log.d(TAG, "Authentication state updated: $state")
    }

    fun resetAuthState() {
        _authenticationState.value = AuthenticationState.Idle
    }

    fun checkBiometricAvailability(): Boolean {
        val biometricManager = BiometricManager.from(getApplication())
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
}

sealed class AuthenticationState {
    object Idle : AuthenticationState()
    object Success : AuthenticationState()
    object Failure : AuthenticationState()
    data class Error(val message: String) : AuthenticationState()
}