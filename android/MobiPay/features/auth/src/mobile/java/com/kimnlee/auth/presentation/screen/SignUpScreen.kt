package com.kimnlee.auth.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kimnlee.common.auth.AuthManager
import android.util.Patterns
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction

@Composable
fun SignUpScreen(
    authManager: AuthManager,
    onNavigateToHome: () -> Unit,
    onNavigateToBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }

    Column {
        Spacer(modifier = Modifier.height(300.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it.trim()
                emailError = !isValidEmail(email)
            },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            isError = emailError,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            )
        )
        if (emailError) {
            Text(
                text = "유효하지 않은 이메일 형식입니다.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (!emailError) {
                    onNavigateToHome() // api 요청 보내기
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(150.dp))
        Button(
            onClick = { onNavigateToBack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("로그인 페이지로 이동")
        }
    }
}

private fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}