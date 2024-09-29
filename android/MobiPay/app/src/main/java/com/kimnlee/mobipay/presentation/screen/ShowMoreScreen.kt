package com.kimnlee.mobipay.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.common.auth.AuthManager

@Composable
fun ShowMoreScreen(
    viewModel: LoginViewModel,
    authManager: AuthManager,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "더 보기",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Button(onClick = {
            navController.navigate("payment")
        }) {
            Text(
                text = "결제페이지",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.padding(16.dp))
            Button(onClick = { navController.navigate("paymenthistory")}) {
                Text(
                    text = "결제 내역",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        Spacer(modifier = Modifier.padding(16.dp))
            Button(onClick = { /*TODO*/ }) {
                Text(
                    text = "설정 3",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        Spacer(modifier = Modifier.padding(16.dp))
            Button(onClick = { /*TODO*/ }) {
                Text(
                    text = "설정 4",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        Spacer(modifier = Modifier.padding(16.dp))
            Button(onClick = { /*TODO*/ }) {
                Text(
                    text = "설정 5",
                    style = MaterialTheme.typography.bodyLarge
                )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.testLogout() // 현재 테스트 로그아웃이라서 나중에 백이랑 연결되면 일반 logout 메서드로 바꾸면 됨
            }
        ) {
            Text("로그아웃")
        }
    }
}
