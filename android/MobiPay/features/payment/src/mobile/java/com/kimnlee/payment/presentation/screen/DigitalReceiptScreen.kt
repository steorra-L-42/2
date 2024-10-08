package com.kimnlee.payment.presentation.screen

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiBgGray
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.ui.theme.MobiTextDarkGray
import com.kimnlee.common.utils.formatCardNumber
import com.kimnlee.common.utils.formatDateTimeWithHyphens
import com.kimnlee.common.utils.moneyFormat
import com.kimnlee.payment.data.model.ReceiptResponse
import com.kimnlee.payment.presentation.viewmodel.ElectronicReceiptState
import com.kimnlee.payment.presentation.viewmodel.PaymentViewModel
import java.util.Locale

private val CardBgColor = Color(0xFF3182F6)
private val DividerColor = Color(0xFFE5E8EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalReceiptScreen(
    onNavigateBack: () -> Unit,
    paymentViewModel: PaymentViewModel,
    transactionUniqueNo: Int
) {
    val electronicReceiptState by paymentViewModel.electronicReceipt.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(transactionUniqueNo) {
        paymentViewModel.clearElectronicReceipt()
        paymentViewModel.loadElectronicReceipt(transactionUniqueNo)
    }

    DisposableEffect(Unit) {
        onDispose {
            paymentViewModel.clearElectronicReceipt()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "💸",
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
        when (electronicReceiptState) {
            is ElectronicReceiptState.Success -> {
                val receipt = (electronicReceiptState as ElectronicReceiptState.Success).data
                ReceiptContent(receipt, innerPadding, context)
            }
            is ElectronicReceiptState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Error: ${(electronicReceiptState as ElectronicReceiptState.Error).message}",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            ElectronicReceiptState.Initial -> {
                // Initial 상태일 때는 아무것도 표시하지 않음
            }
        }
    }
}

@Composable
fun ReceiptContent(receipt: ReceiptResponse, innerPadding: PaddingValues, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 가맹점 이름과 결제 금액
        Text(
            text = receipt.merchantName,
            style = MaterialTheme.typography.headlineSmall,
            color = MobiTextAlmostBlack,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = moneyFormat(receipt.paymentBalance.toBigInteger()),
            style = MaterialTheme.typography.headlineLarge,
            color = MobiTextAlmostBlack,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = DividerColor)
        Spacer(modifier = Modifier.height(24.dp))

        // 결제 상세 정보
        DetailSection(
            items = listOf(
                "공급가액" to moneyFormat(taxCalc(receipt.paymentBalance.toBigInteger(), 10)),
                "부가세" to moneyFormat(taxCalc(receipt.paymentBalance.toBigInteger(), 1)),
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = 1.dp,
            color = DividerColor
        )

        DetailItem("결제금액", moneyFormat(receipt.paymentBalance.toBigInteger()), isTotal = true)

        Spacer(modifier = Modifier.height(32.dp))

        // 거래 정보
        DetailSection(
            items = listOf(
                "카드 종류" to receipt.cardName,
                "카드 번호" to formatCardNumber(receipt.cardNo),
                "거래 일시" to formatDateTimeWithHyphens(receipt.transactionDate, receipt.transactionTime),
                "승인 번호" to receipt.transactionUniqueNo.toString(),
                "가맹점 번호" to receipt.merchantId.toString()
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 가맹점 정보
        DetailSection(
            items = listOf(
                "상호" to receipt.merchantName,
                "주소" to getCurrentAddress(context, receipt.lat, receipt.lng)
            )
        )
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