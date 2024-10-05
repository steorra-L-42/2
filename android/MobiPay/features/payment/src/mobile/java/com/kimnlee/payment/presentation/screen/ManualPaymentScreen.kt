package com.kimnlee.payment.presentation.screen

import android.app.KeyguardManager
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kimnlee.common.FCMData
import com.kimnlee.common.R
import com.kimnlee.common.utils.moneyFormat
import com.kimnlee.payment.presentation.viewmodel.AuthenticationState
import com.kimnlee.payment.presentation.viewmodel.BiometricViewModel

private const val TAG = "ManualPaymentScreen"

@Composable
fun ManualPaymentScreen(
    navController: NavController,
    viewModel: BiometricViewModel = viewModel(),
    fcmData: FCMData?
) {
    val context = LocalContext.current
    val authState by viewModel.authenticationState.collectAsState()
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
//    val bioAuth = viewModel.BIO_AUTH

    val biometricLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            viewModel.updateAuthenticationState(AuthenticationState.Success)
        } else {
            viewModel.updateAuthenticationState(AuthenticationState.Failure)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToPaymentDetail.collect {
            Log.d(TAG, "ManualPaymentScreen: 인증 성공. payment_detail로 이동합니다.")
            navController.navigate("payment_detail")
            viewModel.resetAuthState()
        }
    }

    if(fcmData == null)
        return
    else if(isAnyFieldNull(fcmData))
        return
    // NULL체크 완료

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_mobipay),
            contentDescription = "Coin Icon",
            modifier = Modifier
                .size(100.dp)
                .padding(top = 24.dp)
        )
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mobipay),
                contentDescription = "가맹점 로고",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = fcmData.merchantName!!,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
        }

        Text(
            text = moneyFormat(fcmData.paymentBalance!!.toBigInteger()),
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Button(
            onClick = {
                if (viewModel.checkBiometricAvailability()) {
                    val intent = keyguardManager.createConfirmDeviceCredentialIntent(
                        "생체 인증",
                        "지문을 사용하여 인증해주세요"
                    )
                    if (intent != null) {
                        biometricLauncher.launch(intent)
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
                Log.d(TAG, "ManualPaymentScreen: 인증 성공. 화면 이동 대기중")
                Text("인증 완료", style = MaterialTheme.typography.bodyMedium)  // Change text to "인증 완료"
            }
            is AuthenticationState.Failure -> {
                Log.d(TAG, "ManualPaymentScreen: 인증 실패 Fail")
                Text("인증 실패", style = MaterialTheme.typography.bodyMedium)
            }
            is AuthenticationState.Error -> {
                Log.d(TAG, "ManualPaymentScreen: 인증 오류 Error")
                Text("오류: ${(authState as AuthenticationState.Error).message}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
private fun isAnyFieldNull(fcmData: FCMData): Boolean {
    return with(fcmData) {
        listOf(autoPay, cardNo, approvalWaitingId, merchantId, paymentBalance, merchantName, info, lat, lng, type)
            .any { it == null }
    }
}
