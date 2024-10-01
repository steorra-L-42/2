package com.kimnlee.auth.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.common.ui.theme.MobiBgGray
import com.kimnlee.common.ui.theme.MobiTextDarkGray

@Composable
fun RegistrationScreen(
    viewModel: LoginViewModel,
    onRegistrationSuccess: () -> Unit,
    onRegistrationFailed: () -> Unit,
    onBackPressed: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    val registrationResult by viewModel.registrationResult.collectAsState()

    // 단말기의 뒤로가기 버튼을 눌렀을 때 동작
    BackHandler {
        onBackPressed()
        viewModel.resetStatus() // 로그인 중의 상태 초기화
    }

    LaunchedEffect(registrationResult) {
        when (registrationResult) {
            true -> onRegistrationSuccess()
            false -> onRegistrationFailed()
            null -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            "처음이시군요!\n가입을 위해 아래에 정보를 입력해주세요.",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            "휴대폰 번호",
            fontSize = 14.sp,
            style = MaterialTheme.typography.headlineMedium,
            color = MobiTextDarkGray
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("010-0000-0000") }
        )
        // 나중에 여기에 올바른 전화번호인지 검증 로직 추가예정

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "이름",
            fontSize = 14.sp,
            style = MaterialTheme.typography.headlineMedium,
            color = MobiTextDarkGray
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("홍길동") }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.register(name, phoneNumber) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3182F6)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("확인", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onBackPressed()
                viewModel.resetStatus()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MobiBgGray
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "뒤로가기",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 16.sp,
                color = Color(0xFF3182F6)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}