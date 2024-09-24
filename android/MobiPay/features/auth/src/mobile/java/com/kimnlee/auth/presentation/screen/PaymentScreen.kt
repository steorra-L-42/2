package com.kimnlee.auth.presentation.screen

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kimnlee.auth.presentation.viewmodel.AuthenticationState
import com.kimnlee.auth.presentation.viewmodel.BiometricViewModel
import com.kimnlee.common.R
import java.util.concurrent.Executor

@Composable
fun PaymentScreen(
    navController: NavController,
) {
    val viewModel: BiometricViewModel = viewModel()
    val context = LocalContext.current
//    val activity = context as ComponentActivity // ComponentActivity 사용
    val activity = context as FragmentActivity
    println("11111" + context)
    println("222" + activity)

    LaunchedEffect(Unit) {
        viewModel.initializeBiometric(activity)
    }

    val authState by viewModel.authenticationState.collectAsState()
    println("333" + authState)

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
            Button(onClick = { viewModel.authenticate() }) {
                Text("지문 인증 시작")
            }
            when (authState) {
                is AuthenticationState.Success -> Text("인증 성공!")
                is AuthenticationState.Failure -> Text("인증 실패")
                is AuthenticationState.Error -> Text("오류: ${(authState as AuthenticationState.Error).message}")
                else -> {}
            }
//            when (biometricState) {
//                is BiometricState.None -> {
//                    Button(onClick = { viewModel.authenticateWithBiometric() }) {
//                        Text("지문 인증 시작")
//                    }
//                }
//
//                is BiometricState.Success -> {
//                    Text("인증 성공! 결제를 진행합니다.")
//                }
//
//                is BiometricState.Failed -> {
//                    Text("인증 실패. 다시 시도해주세요.")
//                }
//
//                is BiometricState.Error -> {
//                    Text("오류: ${(biometricState as BiometricState.Error).message}")
//                }
//            }
        }
    }}

//    private fun checkAvailableAuth(context: Context) {
//        val biometricManager = BiometricManager.from(context)
//        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
//            BiometricManager.BIOMETRIC_SUCCESS -> {
//                //  생체 인증 가능
//            }
//
//            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
//                //  기기에서 생체 인증을 지원하지 않는 경우
//            }
//
//            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
//                Log.d("MainActivity", "Biometric facility is currently not available")
//            }
//
//            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
//                //  생체 인식 정보가 등록되지 않은 경우
//            }
//
//            else -> {
//                //   기타 실패
//            }
//        }
//    }