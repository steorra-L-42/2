package com.kimnlee.payment.presentation.screen

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kimnlee.common.R
import com.kimnlee.common.components.DashedDivider
import com.kimnlee.payment.data.dummyMerchants
import com.kimnlee.payment.data.model.MerchantTransaction
import java.util.Locale

@Composable
fun PaymentDetailScreen(
    transaction: MerchantTransaction,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current
        val merchant = dummyMerchants.find { it.merchant_id == transaction.merchant_id }
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onNavigateBack,
            ) {
                Text("뒤로 가기")
            }
            Text(
                text = "전자 영수증",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.padding(16.dp))
        // 기게 이름
        Text(text = merchant!!.merchant_name, style = MaterialTheme.typography.headlineMedium)
        // 소요 금액
        Row {
            Text(
                text = "${transaction.payment_balance}",
                fontWeight = FontWeight(700),
                fontSize = 32.sp,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.alignByBaseline(),
            )
            Text(
                "원",
                modifier = Modifier.alignByBaseline(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.padding(16.dp))
        // 카드 그림
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Image(
                painter = painterResource(id = R.drawable.credit_card_24px),
                contentDescription = "카드 그림",
                modifier = Modifier.background(Color.Gray)
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(text = "싸피 카드 (5612)")
        }
        Spacer(modifier = Modifier.padding(10.dp))
        HorizontalDivider(thickness = 4.dp, color = Color.Black)
        //공급가액 부가세 봉사료
        Column {
            DetailItem(title = "공급가액", content = "${transaction.payment_balance}")
            DetailItem(title = "부가세", content = "0")
            DetailItem(title = "봉사료", content = "0")
        }
        DashedDivider(
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color(0, 0, 0, 50)
        )
        DetailItem(title = "결제금액", content = "${transaction.payment_balance} + 부가세 + 봉사료")

        HorizontalDivider(
            thickness = 8.dp,
            color = Color(148, 148, 148, 50),
            modifier = Modifier.padding(vertical = 16.dp)
        )
        // 거래구분~ 결제금액
        Column {
            DetailItem(title = "거래구분", content = "싸피카드(5612)")
            DetailItem(title = "거래 유형", content = "국내 승인")
            DetailItem(
                title = "거래 일시", content = "${transaction.transaction_date} ${transaction.transaction_time}"
            )
            DetailItem(title = "승인 번호", content = "31961357")
            DetailItem(title = "할부", content = transaction.info)
            DetailItem(title = "가맹점 번호", content = "${transaction.merchant_id}")
            DetailItem(title = "결제 금액", content = "${transaction.payment_balance}")
        }
        DashedDivider(
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color(0, 0, 0, 50)
        )
        // 상호 ~ 주소
        Column {
            DetailItem(title = "상호", content = merchant.merchant_name)
            DetailItem(title = "가맹점 종류", content = merchant.category_id)
            DetailItem("주소", getCurrentAddress(context, merchant.lat, merchant.lng))
        }
    }
}

// 상세 페이지의 항목 하나
@Composable
fun DetailItem(
    title: String, content: String, padding: Int = 10
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = title, color = Color.Gray)
        Text(text = content, fontWeight = FontWeight(500))
    }
}


//위도 경도로 주소 구하는 Reverse-GeoCoding
private fun getCurrentAddress(context: Context, latitude: Double, longitude: Double): String {
//    val addresses: List<Address>?
    try {
        val geocoder = Geocoder(context, Locale.KOREA)
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        // 주소가 존재할 경우
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            return address.getAddressLine(0) // 전체 주소 반환
        }
    } catch (e: Exception) {
        e.printStackTrace() // 예외 발생시 로그 출력
    }
    return ""
}