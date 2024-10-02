package com.kimnlee.mobipay

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
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
import com.kimnlee.payment.PaymentApprovalReceiver

class MainActivity : ComponentActivity() {

    private lateinit var biometricViewModel: BiometricViewModel
    private lateinit var loginViewModel: LoginViewModel

    private var paymentApprovalReceiver: PaymentApprovalReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash 스크린 가동 이후 Theme을 원래대로 되돌리는 코드
        setTheme(R.style.Theme_MobiPay)

        val authManager = (application as MobiPayApplication).authManager
        val apiClient = (application as MobiPayApplication).apiClient
        val fcmService = (application as MobiPayApplication).fcmService

        biometricViewModel = ViewModelProvider(this).get(BiometricViewModel::class.java)

        loginViewModel = LoginViewModel(authManager, apiClient, fcmService)

        registerPayReceiver()

        setContent {
            MobiPayTheme {
                val navController = rememberNavController()
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterPayReceiver()
    }


    private fun registerPayReceiver() {
        val intentFilter = IntentFilter("com.kimnlee.mobipay.PAYMENT_APPROVAL")
        paymentApprovalReceiver = PaymentApprovalReceiver()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(paymentApprovalReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(paymentApprovalReceiver, intentFilter)
        }
    }


    private fun unregisterPayReceiver() {
        paymentApprovalReceiver?.let {
            unregisterReceiver(it)
            paymentApprovalReceiver = null
        }
    }

}