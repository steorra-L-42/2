package com.kimnlee.auth.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    var phoneMiddle by remember { mutableStateOf("") }
    var phoneLast by remember { mutableStateOf("") }
    var phoneNumberError by remember { mutableStateOf("") }

    val registrationResult by viewModel.registrationResult.collectAsState()
    val registrationError by viewModel.registrationError.collectAsState()

    val focusRequesterLast = remember { FocusRequester() }
    val focusRequesterName = remember { FocusRequester() }

    fun validatePhoneNumber(): Boolean {
        return phoneMiddle.length == 4 && phoneLast.length == 4
    }

    fun isValidNameChar(char: Char): Boolean {
        return char.isLetterOrDigit() && (char.isLetter() || char in '가'..'힣')
    }

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

    LaunchedEffect(registrationError) {
        phoneNumberError = registrationError ?: ""
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = "010",
                onValueChange = { },
                modifier = Modifier.weight(1f),
                enabled = false
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = phoneMiddle,
                onValueChange = { newValue ->
                    val filtered = newValue.filter { it.isDigit() }
                    if (filtered.length <= 4) {
                        phoneMiddle = filtered
                        phoneNumberError = "" // 입력 시 에러 메시지 초기화
                        if (filtered.length == 4) {
                            focusRequesterLast.requestFocus()
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = phoneLast,
                onValueChange = { newValue ->
                    val filtered = newValue.filter { it.isDigit() }
                    if (filtered.length <= 4) {
                        phoneLast = filtered
                        phoneNumberError = "" // 입력 시 에러 메시지 초기화
                        if (filtered.length == 4) {
                            focusRequesterName.requestFocus()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequesterLast),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
        if (phoneNumberError.isNotEmpty()) {
            Text(phoneNumberError, color = Color.Red, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "이름",
            fontSize = 14.sp,
            style = MaterialTheme.typography.headlineMedium,
            color = MobiTextDarkGray
        )
        OutlinedTextField(
            value = name,
            onValueChange = { newValue ->
                name = newValue.filter { isValidNameChar(it) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequesterName),
            placeholder = { Text("홍길동") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (!validatePhoneNumber()) {
                    phoneNumberError = "올바른 전화번호 형식이 아니에요."
                } else {
                    val fullPhoneNumber = "010$phoneMiddle$phoneLast"
                    viewModel.register(name, fullPhoneNumber)
                }
            },
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