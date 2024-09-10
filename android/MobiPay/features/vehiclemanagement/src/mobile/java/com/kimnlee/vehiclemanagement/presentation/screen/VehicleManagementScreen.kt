package com.kimnlee.vehiclemanagement.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VehiclemanagementScreen(
    onNavigateToDetail: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "차량 관리",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToDetail) {
            Text("상세 화면으로 이동")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToHome) {
            Text("홈으로 돌아가기")
        }
    }
}