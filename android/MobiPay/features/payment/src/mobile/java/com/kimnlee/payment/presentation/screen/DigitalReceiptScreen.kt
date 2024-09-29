package com.kimnlee.payment.presentation.screen

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiBgGray
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.ui.theme.MobiTextDarkGray
import com.kimnlee.payment.data.dummyMerchants
import com.kimnlee.payment.data.model.MerchantTransaction
import java.util.Locale

private val CardBgColor = Color(0xFF3182F6)
private val DividerColor = Color(0xFFE5E8EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDetailScreen(
    transaction: MerchantTransaction,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val merchant = dummyMerchants.find { it.merchant_id == transaction.merchant_id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🧾",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily(Font(R.font.emoji)),
                            fontSize = 24.sp,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "전자 영수증",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MobiBgGray,
                    titleContentColor = MobiTextAlmostBlack
                )
            )
        },
        containerColor = MobiBgGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 가맹점 이름과 결제 금액
            Text(
                text = merchant?.merchant_name ?: "",
                style = MaterialTheme.typography.headlineSmall,
                color = MobiTextAlmostBlack,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${transaction.payment_balance}원",
                style = MaterialTheme.typography.headlineLarge,
                color = MobiTextAlmostBlack,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 카드 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "싸피 카드 (5612)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MobiTextAlmostBlack
                )
                Image(
                    painter = painterResource(id = R.drawable.credit_card_24px),
                    contentDescription = "카드 그림",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(CardBgColor)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(24.dp))

            // 결제 상세 정보
            DetailSection(
                items = listOf(
                    "공급가액" to "${transaction.payment_balance}원",
                    "부가세" to "0원",
                    "봉사료" to "0원"
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

            DetailItem("결제금액", "${transaction.payment_balance}원", isTotal = true)

            Spacer(modifier = Modifier.height(32.dp))

            // 거래 정보
            DetailSection(
                items = listOf(
                    "거래구분" to "싸피카드(5612)",
                    "거래 유형" to "국내 승인",
                    "거래 일시" to "${transaction.transaction_date} ${transaction.transaction_time}",
                    "승인 번호" to "31961357",
                    "할부" to transaction.info,
                    "가맹점 번호" to "${transaction.merchant_id}"
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 가맹점 정보
            DetailSection(
                items = listOf(
                    "상호" to (merchant?.merchant_name ?: ""),
                    "가맹점 종류" to (merchant?.category_id ?: ""),
                    "주소" to getCurrentAddress(context, merchant?.lat ?: 0.0, merchant?.lng ?: 0.0)
                )
            )
        }
    }
}

@Composable
fun DetailSection(items: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.forEach { (title, content) ->
            DetailItem(title, content)
        }
    }
}

@Composable
fun DetailItem(
    title: String,
    content: String,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isTotal) MobiTextAlmostBlack else MobiTextDarkGray
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MobiTextAlmostBlack,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// 위도 경도로 주소 구하는 Reverse-GeoCoding (기존 코드 유지)
private fun getCurrentAddress(context: Context, latitude: Double, longitude: Double): String {
    try {
        val geocoder = Geocoder(context, Locale.KOREA)
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            return address.getAddressLine(0)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}