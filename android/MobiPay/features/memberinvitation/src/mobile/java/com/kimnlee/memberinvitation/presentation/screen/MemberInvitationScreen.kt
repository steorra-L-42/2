package com.kimnlee.memberinvitation.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MemberInvitationScreen(
    vehicleId: Int,
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


        Button(onClick = onNavigateToDetail) { // 페이지 이동 대신 멤버초대 버튼으로 교체 예정
            Text("상세 화면으로 이동")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "차량 ID: $vehicleId", // 실제 통신시 차량 ID로 차량 정보(차량 번호)를 받아올 예정
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "이 차량에 속한 멤버 표시(이름과 전화번호)" // 첫번째는 오너, 두번째 부터 멤커
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToHome) {
            Text("홈으로 돌아가기")
        }
    }
}