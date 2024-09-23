package com.kimnlee.cardmanagement.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.kimnlee.cardmanagement.R
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.PhotoUiState

@Composable
fun CardManagementScreen(
    onNavigateToDetail: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    viewModel: CardManagementViewModel = viewModel()
) {
    val photoUiState by viewModel.photoUiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "사용자 관리 (카드 관리 모듈)",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.padding(16.dp))
        // API 응답 데이터 표시 photo
        when (val state = photoUiState) {
            // 아직 응답이 오지 않았다면 원형 로딩창 출력
            is PhotoUiState.Loading -> {
                CircularProgressIndicator()
            }
            // 응답 받으면 출력
            is PhotoUiState.Success -> {
                LazyRow (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ){
                    items(state.photos) { photo ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp), // 카드 간의 간격 설정
                            colors = CardDefaults.cardColors(Color.Transparent), // 카드 배경 투명화
                            elevation = CardDefaults.cardElevation(0.dp) // 그림자 제거
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = photo.url ?: painterResource(id = R.drawable.card_example),
                                    contentScale = ContentScale.Crop // 이미지 비율을 유지하면서 크기에 맞게 조정
                                ),
                                contentDescription = photo.title,
                                modifier = Modifier
                                    .clickable { onNavigateToDetail() }
                                    .sizeIn(
                                        minWidth = 350.dp,
                                        minHeight = 100.dp,
                                        maxHeight = 197.dp
                                    )
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.FillWidth, // 카드 크기
                                )
                            Text(text = "albumId: ${photo.albumId}")
                            Text(text = "Website: ${photo.id}")
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(16.dp))
                OutlinedButton(
                    onClick = onNavigateToRegistration ,
                    modifier = Modifier
                        .fillMaxWidth(0.66f)
                        .fillMaxSize(0.33f), // 화면의 1/3 크기
                    shape = RoundedCornerShape(16.dp), // 4개의 각이 둥근 사각형
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add, // "+" 아이콘
                        contentDescription = "Add Card",
                        tint = Color.Gray, // 아이콘 색상 설정
                        modifier = Modifier.fillMaxSize(0.5f)

                    )
                }
            }
            is PhotoUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Button(onClick = { viewModel.fetchPhotos() }) {
                    Text("다시 시도")
                }
            }
        }
    }
}