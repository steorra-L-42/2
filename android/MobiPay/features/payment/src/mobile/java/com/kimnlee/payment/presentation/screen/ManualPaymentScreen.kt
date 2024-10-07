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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kimnlee.common.FCMData
import com.kimnlee.common.R
import com.kimnlee.common.utils.moneyFormat
import com.kimnlee.payment.data.repository.PaymentRepository
import com.kimnlee.payment.presentation.viewmodel.AuthenticationState
import com.kimnlee.payment.presentation.viewmodel.BiometricViewModel

private const val TAG = "ManualPaymentScreen"

@Composable
fun ManualPaymentScreen(
    navController: NavController,
    viewModel: BiometricViewModel = viewModel(),
    fcmData: FCMData?,
    paymentRepository: PaymentRepository,
    registeredCards: String
) {
    val context = LocalContext.current
    val authState by viewModel.authenticationState.collectAsState()
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    val pagerState = rememberPagerState(pageCount = { 3 })
//    val bioAuth = viewModel.BIO_AUTH

    //  1002134901000082

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
            val cardNo = "1002134901000082"
            if (fcmData != null && !isAnyFieldNull(fcmData) && cardNo != null){
                Log.d(TAG, "ManualPaymentScreen: NULL값 없고 카드번호 OK")
                paymentRepository.approveManualPay(fcmData, cardNo)
//                navController.navigate("payment_detail")
                viewModel.resetAuthState()
            }
        }
    }

    if(fcmData == null) {
        Log.d(TAG, "ManualPaymentScreen: FCM 데이터가 NULL 이어서 종료.")
        return
    }else if(isAnyFieldNull(fcmData)) {
        Log.d(TAG, "ManualPaymentScreen: FCM 필드 중 NULL 발견.")
        Log.d(TAG, "ManualPaymentScreen: ${fcmData.toString()}")
        return
    }else{
        Log.d(TAG, "ManualPaymentScreen: FCMData null 아님.")
        Log.d(TAG, "ManualPaymentScreen: 등록 카드정보 잘 넘어옴\n 카드 정보: ${registeredCards}")
    }
    // NULL체크 완료

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(0.5f)
                .fillMaxWidth(),
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 48.dp)
        ) { page ->
//            CardImage(cardInfo = cardInfos[page])
        }
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


data class CardInfo(
    val cardId: Int,
    val cardNo: String
)

fun findCardCompany(cardNumber: String): Int {
    val company = cardNumber.take(4)
    return when (company) {
        "1001" -> R.drawable.kb_only_you_titanium
        "1002" -> R.drawable.s_taptap
        "1003" -> R.drawable.l_im_driving
        "1004" -> R.drawable.w_inyou
        "1006" -> R.drawable.h_energy_plus_edition3
        "1007" -> R.drawable.bc_baro
        "1008" -> R.drawable.nh_zgm_point
        "1009" -> R.drawable.ha_enery_double
        "1010" -> R.drawable.ibk_daily_happy
        else -> R.drawable.card_example
    }
}

@Composable
fun CardImage(cardInfo: CardInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.6f),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Image(
            painter = painterResource(id = findCardCompany(cardInfo.cardNo)),
            contentDescription = "Card Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Text(
            text = maskCardNumber(cardInfo.cardNo),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}


fun maskCardNumber(cardNumber: String): String {

    // 앞뒤 공백이나 - 지우고 처리
    val cleanNumber = cardNumber.replace(Regex("[^0-9]"), "")

    return when (cleanNumber.length) {
        15 -> { // 아멕스
            val first4 = cleanNumber.take(4)
            val last3 = cleanNumber.takeLast(3)
            "$first4${"*".repeat(8)}$last3"
        }
        16 -> { // 일반 카드
            val first4 = cleanNumber.take(4)
            val last4 = cleanNumber.takeLast(4)
            "$first4${"*".repeat(8)}$last4"
        }
        else -> cleanNumber
    }
}

private fun isAnyFieldNull(fcmData: FCMData): Boolean {
    return with(fcmData) {
        // 카드번호는 NULL 체크 하지 않습니다.
        listOf(autoPay, approvalWaitingId, merchantId, paymentBalance, merchantName, info, lat, lng, type)
            .any { it == null }
    }
}
