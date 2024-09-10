package com.kimnlee.auth.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kimnlee.auth.R
import com.kimnlee.common.auth.AuthManager
import kotlinx.coroutines.launch

@Composable
fun AuthMainScreen(
    authManager: AuthManager,
    onNavigateToHome: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // MobiPay 제목
        Text(
            text = "MobiPay",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 자동차 이미지
        Image(
            painter = painterResource(id = R.drawable.genesis_g90), // 실제 이미지 리소스로 변경 필요
            contentDescription = "Car Image",
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 카카오 로그인 버튼
        Button(
            onClick = {
                coroutineScope.launch {
                    // 카카오 로그인 로직 구현 (여기서는 생략)
                    // 로그인 성공 가정
                    authManager.setLoggedIn(true)
                    onNavigateToHome()
                }
                onNavigateToHome()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("카카오 로그인")
        }
    }
}