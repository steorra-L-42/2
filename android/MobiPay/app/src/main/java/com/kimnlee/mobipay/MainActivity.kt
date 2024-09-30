package com.kimnlee.mobipay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.kimnlee.auth.presentation.viewmodel.AuthenticationState
import com.kimnlee.auth.presentation.viewmodel.BiometricViewModel
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.mobipay.navigation.AppNavGraph

class MainActivity : ComponentActivity() {

    private lateinit var biometricViewModel: BiometricViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash 스크린 가동 이후 Theme을 원래대로 되돌리는 코드
        setTheme(R.style.Theme_MobiPay)

        val authManager = (application as MobiPayApplication).authManager
        val apiClient = (application as MobiPayApplication).apiClient
        val fcmService = (application as MobiPayApplication).fcmService

        biometricViewModel = ViewModelProvider(this).get(BiometricViewModel::class.java)
        setContent {
            MobiPayTheme {
                val navController = rememberNavController()
                AppNavGraph(navController, authManager, applicationContext, apiClient, fcmService)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == biometricViewModel.BIO_AUTH) {
            when (resultCode) {
                RESULT_OK -> {
                    biometricViewModel.updateAuthenticationState(AuthenticationState.Success)
//                    Log.d("MainActivity", "Authentication successful")
                }
                RESULT_CANCELED -> {
                    biometricViewModel.updateAuthenticationState(AuthenticationState.Failure)
//                    Log.d("MainActivity", "Authentication canceled or failed")
                }
                else -> {
                    biometricViewModel.updateAuthenticationState(AuthenticationState.Error("Unknown error occurred"))
//                    Log.d("MainActivity", "Authentication error: Unknown result code")
                }
            }
        }
    }
}