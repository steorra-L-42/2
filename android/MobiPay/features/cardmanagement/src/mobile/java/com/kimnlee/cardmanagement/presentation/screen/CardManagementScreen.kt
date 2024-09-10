package com.kimnlee.cardmanagement.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.UserUiState

@Composable
fun CardManagementScreen(
    onNavigateToDetail: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    viewModel: CardManagementViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "사용자 관리 (카드 관리 모듈)",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 기존 버튼들
        Button(onClick = onNavigateToDetail) {
            Text("상세 화면으로 이동")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToRegistration) {
            Text("카드 등록화면으로 이동")
        }
        Button(onClick = onNavigateToHome) {
            Text("홈으로 돌아가기")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // API 응답 데이터 표시
        when (val state = uiState) {
            // 아직 응답이 오지 않았다면 원형 로딩창 출력
            is UserUiState.Loading -> {
                CircularProgressIndicator()
            }
            // 응답 받으면 출력
            is UserUiState.Success -> {
                LazyColumn {
                    items(state.users) { user ->
                        // 아래에 있는 내용들은 custom해서 component로 만들기
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Name: ${user.name}")
                                Text(text = "Username: ${user.username}")
                                Text(text = "Email: ${user.email}")
                                Text(text = "Phone: ${user.phone}")
                                Text(text = "Website: ${user.website}")
                            }
                        }
                    }
                }
            }
            is UserUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Button(onClick = { viewModel.fetchUsers() }) {
                    Text("다시 시도")
                }
            }
        }
    }
}