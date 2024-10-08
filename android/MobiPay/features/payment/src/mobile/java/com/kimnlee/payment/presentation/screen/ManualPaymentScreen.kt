package com.kimnlee.payment.presentation.screen

import android.app.KeyguardManager
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kimnlee.common.FCMData
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiBlue
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.ui.theme.MobiTextDarkGray
import com.kimnlee.common.ui.theme.pBold
import com.kimnlee.common.ui.theme.pMedium
import com.kimnlee.common.ui.theme.pSemiBold
import com.kimnlee.common.ui.theme.tossEmoji
import com.kimnlee.common.utils.moneyFormat
import com.kimnlee.payment.data.model.RegisteredCard
import com.kimnlee.payment.data.repository.PaymentRepository
import com.kimnlee.payment.presentation.viewmodel.AuthenticationState
import com.kimnlee.payment.presentation.viewmodel.BiometricViewModel
import com.kimnlee.common.utils.findCardCompanyName

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
    var registeredCardList: List<RegisteredCard>? = null
    val pagerState = rememberPagerState(pageCount = { registeredCardList?.size!! })

    val biometricLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            viewModel.updateAuthenticationState(AuthenticationState.Success)
            Log.d(TAG, "ManualPaymentScreen: 지문 인증 성공. 홈 화면으로 이동합니다.")
            navController.navigate("home") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        } else {
            viewModel.updateAuthenticationState(AuthenticationState.Failure)
            Log.d(TAG, "ManualPaymentScreen: 지문인식 실패")
        }
    }

    fun triggerBiometricPrompt() {
        if (viewModel.checkBiometricAvailability()) {
            val intent = keyguardManager.createConfirmDeviceCredentialIntent(
                "본인 확인",
                "결제를 위해 지문으로 인증해주세요."
            )
            if (intent != null) {
                biometricLauncher.launch(intent)
            } else {
                viewModel.updateAuthenticationState(AuthenticationState.Error("지문인식을 사용할 수 없습니다."))
            }
        } else {
            viewModel.updateAuthenticationState(AuthenticationState.Error("지문인식을 사용할 수 없습니다."))
        }
    }

    LaunchedEffect(Unit) {
        paymentRepository.getRegisteredCards()
        triggerBiometricPrompt()

        viewModel.navigateToPaymentDetail.collect {
            Log.d(TAG, "ManualPaymentScreen: 인증 성공. payment_detail로 이동합니다.")
            val selectedCard = registeredCardList?.get(pagerState.currentPage)
            val selectedCardNo = selectedCard?.cardNo
            if (fcmData != null && !isAnyFieldNull(fcmData)) {
                Log.d(TAG, "ManualPaymentScreen: fcmData 모든 필드 정상")
                if (selectedCardNo != null) {
                    Log.d(TAG, "ManualPaymentScreen: 카드번호 정상. 결제 완료 처리 시작")
                    paymentRepository.approveManualPay(fcmData, selectedCardNo)
                    viewModel.resetAuthState()
                }
            } else {
                Log.d(TAG, "ManualPaymentScreen: fcmData NULL 발견")
            }
        }
    }

    val gson = Gson()
    val listType = object : TypeToken<List<RegisteredCard>>() {}.type
    registeredCardList = gson.fromJson(registeredCards, listType)

    if (fcmData == null) {
        Log.d(TAG, "ManualPaymentScreen: FCM 데이터가 NULL 이어서 종료.")
        return
    } else if (registeredCardList == null) {
        Log.d(TAG, "ManualPaymentScreen: 등록 카드 목록이 NULL 이어서 종료.")
        return
    } else if (isAnyFieldNull(fcmData)) {
        Log.d(TAG, "ManualPaymentScreen: FCM 필드 중 NULL 발견.")
        Log.d(TAG, "ManualPaymentScreen: ${fcmData.toString()}")
        return
    } else {
        Log.d(TAG, "ManualPaymentScreen: FCMData null 아님, 등록된 카드 NULL 아님")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(7.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
        ){
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "\uD83D\uDCB3",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = tossEmoji,
                fontSize = 32.sp,
                color = MobiTextDarkGray,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "결제",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = pBold,
                fontSize = 32.sp,
                color = MobiTextDarkGray
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth(),
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) { page ->
            val cardInfo = registeredCardList!![page]
            val cardNo = cardInfo.cardNo
            Column() {
                CardImage(cardInfo = cardInfo)
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "${findCardCompanyName(cardNo)} *${cardNo.substring(cardNo.length - 3, cardNo.length)}",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = pMedium,
                    fontSize = 18.sp,
                    color = MobiTextDarkGray
                )
            }
        }

        LaunchedEffect(pagerState.currentPage) {
            val selectedCard = registeredCardList!![pagerState.currentPage]
            Log.d(TAG, "사용자가 선택한 카드번호: ${selectedCard.cardNo}")
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            modifier = Modifier.padding(vertical = 6.dp),
            text = fcmData.merchantName!!,
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = pSemiBold,
            color = MobiTextAlmostBlack
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            modifier = Modifier.padding(vertical = 6.dp),
            text = fcmData.info!!,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = pSemiBold,
            color = MobiTextDarkGray
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = moneyFormat(fcmData.paymentBalance!!.toBigInteger()),
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = pBold,
            color = MobiTextAlmostBlack,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                triggerBiometricPrompt()
            },
            modifier = Modifier
                .heightIn(min = 52.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MobiBlue,
                contentColor = Color.White
            )
        ) {
            Text(
                "결제하기",
                fontFamily = pBold,
                color = Color.White,
                fontSize = 20.sp
            )
        }
    }
}


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
fun CardImage(cardInfo: RegisteredCard) {
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
        listOf(autoPay,

            approvalWaitingId, merchantId, paymentBalance, merchantName, info, lat, lng, type)
            .any { it == null }
    }
}
