package com.kimnlee.mobipay.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kimnlee.common.auth.AuthManager

@Composable
fun SettingScreen(
    authManager: AuthManager,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "설정",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
