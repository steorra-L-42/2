package com.kimnlee.cardmanagement.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kimnlee.cardmanagement.R
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.UserUiState

@Composable
fun CardManagementScreen(
    onNavigateToDetail: () -> Unit,
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
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.padding(16.dp))
        // 카드 모음
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(id = R.drawable.card_example),
                contentDescription = "카드 이지",
                modifier = Modifier
                    .graphicsLayer(scaleX = 1.2f, scaleY = 1.2f)
                    .clickable { onNavigateToDetail() }
                    ,
                contentScale = ContentScale.Crop,

            )
            Spacer(modifier = Modifier.height(48.dp))
            OutlinedButton(
                onClick = onNavigateToRegistration ,
                        modifier = Modifier
                            .fillMaxWidth(0.66f)
                            .fillMaxSize(0.33f), // 화면의 1/3 크기
                shape = RoundedCornerShape(16.dp), // 4개의 각이 둥근 사각형
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray),
            ) {
                Icon(
                    imageVector = Icons.Default.Add, // "+" 아이콘
                    contentDescription = "Add Card",
                    tint = Color.Gray, // 아이콘 색상 설정
                    modifier = Modifier.fillMaxSize(0.5f)

                )
            }
        }
        Spacer(modifier = Modifier.height(48.dp))

        // API 응답 데이터 표시
        when (val state = uiState) {
            // 아직 응답이 오지 않았다면 원형 로딩창 출력
            is UserUiState.Loading -> {
                CircularProgressIndicator()
            }
            // 응답 받으면 출력
            is UserUiState.Success -> {
                LazyColumn (
//                    modifier = Modifier.fillMaxSize(),
//                    verticalAlignment = Alignment.CenterVertically

                ){
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