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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

    LaunchedEffect(Unit) {
        viewModel.navigateToPaymentDetail.collect {
            navController.navigate("payment_detail")
            viewModel.resetAuthState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Icon (Coin)
        Image(
            painter = painterResource(id = R.drawable.ic_mobipay),
            contentDescription = "Coin Icon",
            modifier = Modifier
                .size(100.dp)
                .padding(top = 24.dp)
        )

        // Store Information (Logo + Name)
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mobipay), // Replace with your actual McDonald's logo drawable
                contentDescription = "McDonald's Logo",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "맥도날드 구미 인동점",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
        }

        // Price
        Text(
            text = "13,500 원",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Fingerprint Authentication Button
        Button(
            onClick = {
                if (viewModel.checkBiometricAvailability()) {
                    val intent = keyguardManager.createConfirmDeviceCredentialIntent(
                        "생체 인증",
                        "지문을 사용하여 인증해주세요"
                    )
                    if (intent != null) {
                        (context as ComponentActivity).startActivityForResult(
                            intent, bioAuth
                        )
                    } else {
                        viewModel.updateAuthenticationState(AuthenticationState.Error("생체 인증을 사용할 수 없습니다."))
                    }
                } else {
                    viewModel.updateAuthenticationState(AuthenticationState.Error("생체 인증을 사용할 수 없습니다."))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
                .height(60.dp)
        ) {
            Text(text = "지문 인증 시작", style = MaterialTheme.typography.bodyLarge)
        }

        when (authState) {
            is AuthenticationState.Idle -> Text("인증 대기 중", style = MaterialTheme.typography.bodyMedium)
            is AuthenticationState.Success -> {
                Text("인증 성공!", style = MaterialTheme.typography.bodyMedium)
            }
            is AuthenticationState.Failure -> {
                Text("인증 실패!", style = MaterialTheme.typography.bodyMedium)
            }
            is AuthenticationState.Error -> {
                Text("오류: ${(authState as AuthenticationState.Error).message}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

