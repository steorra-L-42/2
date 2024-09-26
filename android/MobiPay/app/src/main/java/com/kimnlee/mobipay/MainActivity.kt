package com.kimnlee.mobipay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.kimnlee.common.auth.repository.ApiUnAuthService
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.mobipay.navigation.AppNavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authManager = (application as MobiPayApplication).authManager

        setContent {
            MobiPayTheme {
                val navController = rememberNavController()
                AppNavGraph(navController, authManager)
            }
        }
    }
}