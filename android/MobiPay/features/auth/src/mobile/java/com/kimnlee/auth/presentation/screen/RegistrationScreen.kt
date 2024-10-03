package com.kimnlee.auth.presentation.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.DefaultTintColor
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kimnlee.auth.R
import com.kimnlee.auth.presentation.components.PrivacyPolicyModal
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
    var privacyText by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phoneMiddle by remember { mutableStateOf("") }
    var phoneLast by remember { mutableStateOf("") }
    var phoneNumberError by remember { mutableStateOf("") }

    val registrationResult by viewModel.registrationResult.collectAsState()
    val registrationError by viewModel.registrationError.collectAsState()

    val focusRequesterLast = remember { FocusRequester() }
    val focusRequesterName = remember { FocusRequester() }

    val showPolicyModal by viewModel.showPolicyModal.collectAsState()
    val hasAgreed by viewModel.hasAgreed.collectAsState()
    var hasAgreedError by remember { mutableStateOf("") }

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

    val context = LocalContext.current

    fun getPrivacyText(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
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
        Spacer(modifier = Modifier.height(12.dp))
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
                modifier = Modifier
                    .weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MobiBgGray,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = MobiTextDarkGray,
                    focusedTrailingIconColor = MobiTextDarkGray,
                    cursorColor = MobiTextDarkGray,
                    focusedIndicatorColor = MobiTextDarkGray
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
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
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MobiBgGray,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = MobiTextDarkGray,
                    focusedTrailingIconColor = MobiTextDarkGray,
                    cursorColor = MobiTextDarkGray,
                    focusedIndicatorColor = MobiTextDarkGray,
                )
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
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { newValue ->
                name = newValue.filter { isValidNameChar(it) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequesterName),
            placeholder = { Text("홍길동") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MobiBgGray,
                unfocusedContainerColor = Color.White,
                focusedTextColor = MobiTextDarkGray,
                focusedTrailingIconColor = MobiTextDarkGray,
                cursorColor = MobiTextDarkGray,
                focusedIndicatorColor = MobiTextDarkGray,
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Box() {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = hasAgreed,
                        onCheckedChange = { viewModel.tooglePolicy() },
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp)) // 체크박스와 텍스트 사이 간격
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "모비페이 ",
                            fontSize = 9.sp,
                            modifier = Modifier
                                .alignByBaseline()
                        )
                        Text(text = "이용약관",
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline,
                            fontSize = 9.sp,
                            modifier = Modifier
                                .clickable {
                                    privacyText = getPrivacyText("privacy_text.txt")
                                    viewModel.openPrivacyModal()
                                }
                                .height(IntrinsicSize.Min)
                                .alignByBaseline()
                        )
                        Text(
                            "및", fontSize = 8.sp,
                            modifier = Modifier
                                .alignByBaseline()
                                .padding(horizontal = 1.dp)
                        )
                        Text(
                            "자동 결제 약관",
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline,
                            fontSize = 9.sp,
                            modifier = Modifier
                                .alignByBaseline()
                                .height(IntrinsicSize.Min)
                                .clickable {
                                    privacyText = getPrivacyText("payment_text.txt")
                                    viewModel.openPrivacyModal()
                                }
                        )
                        Text("에 동의합니다", fontSize = 9.sp, modifier = Modifier.alignByBaseline())
                    }
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    onClick = {
                        if (!validatePhoneNumber()) {
                            phoneNumberError = "올바른 전화번호 형식이 아니에요."
                        } else {
                            val fullPhoneNumber = "010$phoneMiddle$phoneLast"
                            viewModel.register(name, fullPhoneNumber)
                        }
                    },
                    enabled = hasAgreed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3182F6)
                    ),

                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("확인", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        onBackPressed()
                        viewModel.resetStatus()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
//                    colors = ButtonDefaults.buttonColors(containerColor = MobiBgGray),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "뒤로가기",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 12.sp,
                        color = MobiTextDarkGray
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(40.dp))
    if (showPolicyModal) {
        PrivacyPolicyModal(viewModel = viewModel, privacyText = privacyText)
    }
}
