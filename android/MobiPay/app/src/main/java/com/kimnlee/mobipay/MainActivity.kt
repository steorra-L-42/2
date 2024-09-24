package com.kimnlee.mobipay

import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.mobipay.navigation.AppNavGraph
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authManager = AuthManager(this)
        setContent {
            MobiPayTheme {
                val navController = rememberNavController()
                AppNavGraph(navController, authManager, applicationContext)
            }
        }
    }
}