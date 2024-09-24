package com.kimnlee.auth.presentation.viewmodel

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BiometricViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private val _authenticationState = MutableStateFlow<AuthenticationState>(AuthenticationState.Idle)
    val authenticationState: StateFlow<AuthenticationState> = _authenticationState

    fun initializeBiometric(activity: FragmentActivity) {
        val executor = ContextCompat.getMainExecutor(activity)
        biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    _authenticationState.value = AuthenticationState.Success
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    _authenticationState.value = AuthenticationState.Error(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    _authenticationState.value = AuthenticationState.Failure
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("생체인증")
            .setSubtitle("지문을 사용하여 인증해주세요")
            .setNegativeButtonText("취소")
            .build()
    }

    fun authenticate() {
        biometricPrompt.authenticate(promptInfo)
    }
}

sealed class AuthenticationState {
    object Idle : AuthenticationState()
    object Success : AuthenticationState()
    object Failure : AuthenticationState()
    data class Error(val message: String) : AuthenticationState()
}