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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    authManager: AuthManager,
    onNavigateToHome: () -> Unit,
    onNavigateToBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var signUpSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(signUpSuccess) {
        if (signUpSuccess) {
            onNavigateToHome()
        }
    }

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
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (!emailError) {
                        isLoading = true
                        CoroutineScope(Dispatchers.Main).launch {
                            signUp(email, authManager, { signUpSuccess = true }, { errorMessage = it }) {
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("회원가입")
            }
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

private suspend fun signUp(
    email: String,
    authManager: AuthManager,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    onFinally: () -> Unit
) {
    try {
        val response = authManager.signUp(email)
        if (response.success) {
            onSuccess()
        } else {
            onError(response.message)
        }
    } catch (e: Exception) {
        onError("회원가입 중 오류가 발생했습니다.")
    } finally {
        onFinally()
    }
}