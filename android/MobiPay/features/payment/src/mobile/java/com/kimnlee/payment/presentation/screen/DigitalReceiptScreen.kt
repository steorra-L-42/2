package com.kimnlee.payment.presentation.screen

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiBgGray
import com.kimnlee.common.ui.theme.MobiBgWhite
import com.kimnlee.common.ui.theme.MobiBlue
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.ui.theme.MobiTextDarkGray
import com.kimnlee.common.utils.formatCardNumber
import com.kimnlee.common.utils.formatDateTimeWithHyphens
import com.kimnlee.common.utils.moneyFormat
import com.kimnlee.payment.data.model.ReceiptResponse
import com.kimnlee.payment.presentation.viewmodel.ElectronicReceiptState
import com.kimnlee.payment.presentation.viewmodel.PaymentViewModel
import java.util.Locale

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
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "전자 영수증",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MobiBgGray)
        ) {
            when (electronicReceiptState) {
                is ElectronicReceiptState.Success -> {
                    val receipt = (electronicReceiptState as ElectronicReceiptState.Success).data
                    ReceiptCard(receipt, context)
                }
                is ElectronicReceiptState.Error -> {}
                ElectronicReceiptState.Initial -> {}
            }
        }
    }
}

// 왼쪽 정렬 SingleLineDetailItem (카드 번호용)
@Composable
fun LeftAlignedDetailItem(content: String) {
    Text(
        text = content,
        style = MaterialTheme.typography.bodyMedium,
        color = MobiTextAlmostBlack,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

// 오른쪽 정렬 SingleLineDetailItem (가맹점 주소용)
@Composable
fun RightAlignedDetailItem(content: String) {
    Text(
        text = content,
        style = MaterialTheme.typography.bodySmall,
        color = MobiTextAlmostBlack,
        textAlign = TextAlign.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun ReceiptCard(receipt: ReceiptResponse, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MobiBgWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ReceiptHeader(receipt)
            PaymentAmountSection(receipt)
            PaymentDetailsSection(receipt)
            CardInfoSection(receipt)
            MerchantInfoSection(receipt, context)
        }
    }
}

@Composable
fun ReceiptHeader(receipt: ReceiptResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MobiBgWhite)
            .padding(16.dp)
    ) {
        Text(
            text = receipt.merchantName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MobiTextAlmostBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formatDateTimeWithHyphens(receipt.transactionDate, receipt.transactionTime),
            style = MaterialTheme.typography.bodyMedium,
            color = MobiTextDarkGray
        )
    }
}

@Composable
fun PaymentAmountSection(receipt: ReceiptResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE8F3FF))
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = "결제 금액",
            style = MaterialTheme.typography.titleMedium,
            color = MobiTextDarkGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = moneyFormat(receipt.paymentBalance.toBigInteger()),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MobiBlue
        )
    }
}

@Composable
fun PaymentDetailsSection(receipt: ReceiptResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        DetailItem("공급가액", moneyFormat(taxCalc(receipt.paymentBalance.toBigInteger(), 10)))
        DetailItem("부가세", moneyFormat(taxCalc(receipt.paymentBalance.toBigInteger(), 1)))
        Divider(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .height(2.dp)
                .background(Color(0xFFE8F3FF))  // 연한 파란색 구분선
        )
        DetailItem("합계", moneyFormat(receipt.paymentBalance.toBigInteger()), isTotal = true)
    }
}

@Composable
fun CardInfoSection(receipt: ReceiptResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Check Icon",
                tint = MobiTextDarkGray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "결제 수단",
                style = MaterialTheme.typography.titleMedium,
                color = MobiTextDarkGray
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LeftAlignedDetailItem("${receipt.cardName} (${formatCardNumber(receipt.cardNo)})")
        DetailItem("승인번호", receipt.transactionUniqueNo.toString())
    }
}

@Composable
fun MerchantInfoSection(receipt: ReceiptResponse, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Cart Icon",
                tint = MobiTextDarkGray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "가맹점 정보",
                style = MaterialTheme.typography.titleMedium,
                color = MobiTextDarkGray
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        DetailItem("상호", receipt.merchantName)
        RightAlignedDetailItem(getCurrentAddress(context, receipt.lat, receipt.lng))
        DetailItem("가맹점 번호", receipt.merchantId.toString())
    }
}

@Composable
fun DetailItem(title: String, content: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isTotal) MobiTextAlmostBlack else MobiTextDarkGray
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = MobiTextAlmostBlack
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