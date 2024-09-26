package com.kimnlee.memberinvitation.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MemberInvitationDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToConfirmation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "근처 멤버 초대",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateBack) {
            Text("뒤로 가기")
        }

        // 테스트 코드 추가(구현 시 삭제 예정)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToConfirmation) {
            Text("멤버 초대 확인 페이지로 이동")
        }
    }
}