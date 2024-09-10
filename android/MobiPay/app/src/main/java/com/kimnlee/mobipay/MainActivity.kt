package com.kimnlee.mobipay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.mobipay.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobiPayTheme {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }
}