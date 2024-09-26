package com.kimnlee.auth.presentation.screen

import android.app.KeyguardManager
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kimnlee.auth.presentation.viewmodel.AuthenticationState
import com.kimnlee.auth.presentation.viewmodel.BiometricViewModel
import com.kimnlee.common.R

private val TAG = "Payment Screen"

@Composable
fun PaymentScreen(
    navController: NavController,
    viewModel: BiometricViewModel = viewModel(),
) {
    val context = LocalContext.current
    val authState by viewModel.authenticationState.collectAsState()
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    val bioAuth = viewModel.BIO_AUTH

    println("context?????? $context")
    println("authState????? $authState")
    println("keyguardManager?????? $keyguardManager")

    LaunchedEffect(Unit) {
        viewModel.navigateToPaymentDetail.collect {
            navController.navigate("payment_detail")
            viewModel.resetAuthState()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 제목
        Text(text = "결제 페이지")
        // 코인 사진
        Image(
            painter = painterResource(id = R.drawable.settings_24px),
            contentDescription = "영수증 사진",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .weight(0.3f)
                .padding(50.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .height(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Image(
                painter = painterResource(id = R.drawable.credit_card_24px),
                contentDescription = "카드 사진",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()

                    .background(Color.Gray)
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Text(
                text = "맥도날드 구미 인동점",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        // 가격
        Text(
            text = "13,500원",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .weight(0.1f)
                .background(Color.Red)
                .fillMaxHeight()
                .wrapContentHeight(align = Alignment.CenterVertically),
        )
        // 지문 박스
        Box(
            modifier = Modifier
                .weight(0.2f)
                .fillMaxSize()
                .background(Color.Gray)
        ) {
            Button(onClick = {
                if (viewModel.checkBiometricAvailability()) {
                    val intent = keyguardManager.createConfirmDeviceCredentialIntent(
                        "생체 인증",
                        "지문을 사용하여 인증해주세요"
                    )
                    if (intent != null) {
                        (context as ComponentActivity).startActivityForResult(
                            intent, bioAuth
                        )
                        Log.e(TAG, "intent가 널이 아니다.")
                    } else {
                        viewModel.updateAuthenticationState(AuthenticationState.Error("생체 인증을 사용할 수 없습니다."))
                    }
                } else {
                    viewModel.updateAuthenticationState(AuthenticationState.Error("생체 인증을 사용할 수 없습니다."))
                }
            }) {
                Text("지문 인증 시작")
            }
        }
        when (authState) {
            is AuthenticationState.Idle -> Text("인증 대기 중")
            is AuthenticationState.Success -> {
                Text("인증 성공!")
                Log.d(TAG, "AuthenticationState.Success 했음")
            }

            is AuthenticationState.Failure -> {
                Text("인증 실패!")
                Log.d(TAG, "AuthenticationState.fail 했음")
            }

            is AuthenticationState.Error -> {
                Text("오류: ${(authState as AuthenticationState.Error).message}")
                Log.d(TAG, "오류: ${(authState as AuthenticationState.Error).message}")
            }
        }
    }
}

