package com.kimnlee.auth.presentation.screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.kimnlee.auth.R
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val context = LocalContext.current

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Spacer(modifier = Modifier.height(screenHeight * 0.05f))

            Text(
                text = "MobiPay",
                fontSize = min(screenWidth * 0.08f, 32.dp).value.sp,
                fontWeight = FontWeight.Bold
            )

            // 자동차 이미지 크기 증가
            Image(
                painter = painterResource(id = R.drawable.genesis_g90),
                contentDescription = "Car Image",
                modifier = Modifier.size(min(screenWidth * 0.8f, 400.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.weight(1f))

            Button( // 테스트를 위해 생성(로그인 구현 시 삭제 예정)
                onClick = { onNavigateToSignUp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("회원가입(개발용)")
            }
            Button( // 테스트 로그인 버튼으로 나중에 백엔드랑 연결되면 삭제 예정
                onClick = { viewModel.testLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("테스트 로그인")
            }

            Surface(
                onClick = { viewModel.login(context as Activity) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(min(screenHeight * 0.08f, 56.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kakao_login_large_wide),
                    contentDescription = "카카오 로그인",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.05f))
        }
    }
}