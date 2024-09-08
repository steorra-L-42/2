package com.kimnlee.memberinvitation.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MemberInvitationScreen(
    onNavigateToDetail: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "멤버 초대",
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