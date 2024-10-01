package com.kimnlee.mobipay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.kimnlee.auth.presentation.viewmodel.AuthenticationState
import com.kimnlee.auth.presentation.viewmodel.BiometricViewModel
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.common.FCMData
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.mobipay.navigation.AppNavGraph

class MainActivity : ComponentActivity() {

    private lateinit var biometricViewModel: BiometricViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash 스크린 가동 이후 Theme을 원래대로 되돌리는 코드
        setTheme(R.style.Theme_MobiPay)

        val authManager = (application as MobiPayApplication).authManager
        val apiClient = (application as MobiPayApplication).apiClient
        val fcmService = (application as MobiPayApplication).fcmService

        biometricViewModel = ViewModelProvider(this).get(BiometricViewModel::class.java)

        val fcmDataJson = intent?.getStringExtra("fcmData")
        val fcmData = fcmDataJson?.let { Gson().fromJson(it, FCMData::class.java) }

        loginViewModel = LoginViewModel(authManager, apiClient, fcmService)

        setContent {
            MobiPayTheme {
                val navController = rememberNavController()
//                AppNavGraph(navController, authManager, applicationContext, apiClient, fcmService)
                AppNavGraph(navController, authManager, applicationContext, apiClient, fcmService)
                val isLoggedIn by authManager.isLoggedIn.collectAsState(initial = false)

                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn) {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }

                AppNavGraph(
                    navController,
                    authManager,
                    applicationContext,
                    apiClient,
                    loginViewModel
                )
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}