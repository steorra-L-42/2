package com.kimnlee.memberinvitation.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun MemberInvitationScreen(
    vehicleId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateBack) {
            Text("뒤로 가기")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "멤버 초대",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToDetail) { // 근처 멤버 추가하기 페이지로 이동
            Text("근처 멤버 초대하기")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("전화번호로 초대하기") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            trailingIcon = { // 전화번호 검색 시 가입된 유저이면 초대 알림 전송, 아니면 알림 띄우기
                IconButton(onClick = { /* 전화번호 초대 로직 구현 */ }) {
                    Icon(Icons.Default.Search, contentDescription = "추가")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "차량 ID: $vehicleId", // 실제 통신시 차량 ID로 차량 정보(차량 번호)를 받아올 예정
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "이 차량에 속한 멤버 표시(이름과 전화번호)" // 첫번째는 오너, 두번째 부터 멤버
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToHome) {
            Text("홈으로 돌아가기")
        }
    }
}