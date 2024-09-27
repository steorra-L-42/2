package com.kimnlee.mobipay

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.kimnlee.auth.presentation.viewmodel.BiometricViewModel
import com.kimnlee.auth.presentation.viewmodel.AuthenticationState
import com.kimnlee.common.auth.repository.ApiUnAuthService
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.mobipay.navigation.AppNavGraph
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var biometricViewModel: BiometricViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authManager = (application as MobiPayApplication).authManager

        biometricViewModel = ViewModelProvider(this).get(BiometricViewModel::class.java)
        setContent {
            MobiPayTheme {
                val navController = rememberNavController()
                AppNavGraph(navController, authManager, applicationContext)
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