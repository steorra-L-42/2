package com.kimnlee.auth.presentation.screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val context = LocalContext.current

    val tossBlue = Color(0xFF3182F6)
    val tossGray = Color(0xFFF2F3F5)

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onNavigateToHome()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "MobiPay",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = tossBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "차량 결제의 혁신",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }

            // 자동차 이미지
            Image(
                painter = painterResource(id = R.drawable.genesis_g90),
                contentDescription = "Car Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .padding(vertical = 24.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 카카오 로그인 버튼 (기존 이미지 사용, 비율 조정)
                Image(
                    painter = painterResource(id = R.drawable.kakao_login_large_wide),
                    contentDescription = "카카오 로그인",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable { viewModel.login(context as Activity) }
                )

                // 회원가입 버튼 (토스 스타일)
                Button(
                    onClick = { onNavigateToSignUp() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = tossBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("회원가입", fontSize = 16.sp, color = Color.White)
                }

                // 테스트 로그인 버튼
                TextButton(
                    onClick = { viewModel.testLogin() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("테스트 로그인", fontSize = 14.sp, color = tossBlue)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}