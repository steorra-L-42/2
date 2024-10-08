package com.kimnlee.payment.presentation.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kimnlee.common.FCMData
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.common.ui.theme.pBold
import com.kimnlee.common.ui.theme.pMedium
import com.kimnlee.common.ui.theme.pRegular
import com.kimnlee.common.utils.moneyFormat
import com.kimnlee.common.utils.findCardCompanyName
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "PaymentCancelScreen"

@Composable
fun PaymentCancelScreen(
    navController: NavController,
    fcmData: FCMData?
) {

    if (fcmData == null) {
        Log.d(TAG, "PaymentCancelScreen: FCM 데이터가 NULL 이어서 종료.")
        return
    } else if (isAnyFieldNull(fcmData)) {
        Log.d(TAG, "PaymentCancelScreen: FCM 필드 중 NULL 발견.")
        Log.d(TAG, "PaymentCancelScreen: ${fcmData.toString()}")
        return
    } else {
        Log.d(TAG, "PaymentCancelScreen: FCMData null 아님")
    }

    MobiPayTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.height(66.dp))

            Image(
                painter = painterResource(id = R.drawable.refund),
                contentDescription = "결제취소 아이콘",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "결제취소 완료",
                fontSize = 28.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontFamily = pBold,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "가맹점에서 결제를 취소했어요.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontFamily = pRegular
            )

            Spacer(modifier = Modifier.height(40.dp))

            PaymentCancelDetailsCard(fcmData)

            Spacer(modifier = Modifier.height(80.dp))

            ReturnToMainButton(navController)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PaymentCancelDetailsCard(fcmData: FCMData?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            PaymentCancelDetailRow("가맹점명", fcmData?.merchantName ?: "맥도날드 동탄나루점")
            Spacer(modifier = Modifier.height(14.dp))
            PaymentCancelDetailRow("일시", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")))
            Spacer(modifier = Modifier.height(14.dp))
            val cardNo = fcmData!!.cardNo!!
            PaymentCancelDetailRow("결제 카드", "${findCardCompanyName(cardNo)} *${cardNo.substring(cardNo.length-3, cardNo.length)}")
            Spacer(modifier = Modifier.height(14.dp))
            PaymentCancelDetailRow("금액", moneyFormat(taxCalc(BigInteger(fcmData?.paymentBalance), 10)))
            Spacer(modifier = Modifier.height(14.dp))
            PaymentCancelDetailRow("부가세", moneyFormat(taxCalc(BigInteger(fcmData?.paymentBalance), 1)))
            Spacer(modifier = Modifier.height(14.dp))
            PaymentCancelDetailRow("합계", moneyFormat(BigInteger(fcmData?.paymentBalance)))
        }
    }
}


@Composable
fun PaymentCancelDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = TextStyle(
            fontSize = 16.sp,
            color = Color.Gray,
            fontFamily = pRegular
        )
        )
        Text(value, style = TextStyle(
            fontSize = 16.sp,
            fontFamily = pMedium
        )
        )
    }
}

private fun isAnyFieldNull(fcmData: FCMData): Boolean {
    return with(fcmData) {
        listOf(cardNo, paymentBalance, merchantName, type)
            .any { it == null }
    }
}
