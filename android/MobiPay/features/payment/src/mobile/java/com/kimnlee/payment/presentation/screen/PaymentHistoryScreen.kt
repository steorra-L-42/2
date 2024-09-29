package com.kimnlee.payment.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kimnlee.common.R
import com.kimnlee.payment.data.model.Merchant
import com.kimnlee.payment.data.model.MerchantTransaction

// 토스 스타일 색상 정의(임시)
private val TossBackgroundColor = Color(0xFFF9FAFB)
private val TossPrimaryColor = Color(0xFF3182F6)
private val TossTextColor = Color(0xFF191F28)
private val TossSecondaryTextColor = Color(0xFF8B95A1)
private val TossCardBackgroundColor = Color.White

@Composable
fun PaymentDetailListScreen(
    transactions: List<MerchantTransaction>,
    merchants: List<Merchant>,
    onNavigateToDetail: (transaction: MerchantTransaction) -> Unit,
    onNavigateBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = TossBackgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "결제 내역",
                style = MaterialTheme.typography.headlineMedium,
                color = TossTextColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(transactions) { transaction ->
                    TransactionCard(
                        transaction = transaction,
                        merchant = merchants.find { it.merchant_id == transaction.merchant_id },
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionCard(
    transaction: MerchantTransaction,
    merchant: Merchant?,
    onNavigateToDetail: (transaction: MerchantTransaction) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToDetail(transaction) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = TossCardBackgroundColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = merchant?.merchant_name ?: "Unknown Merchant",
                    style = MaterialTheme.typography.titleMedium,
                    color = TossTextColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${transaction.transaction_date} ${transaction.transaction_time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TossSecondaryTextColor
                )
                Text(
                    text = transaction.info,
                    style = MaterialTheme.typography.bodySmall,
                    color = TossSecondaryTextColor
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${transaction.payment_balance}원",
                    style = MaterialTheme.typography.titleMedium,
                    color = TossTextColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Image(
                    painter = painterResource(id = R.drawable.receipt_long_24px),
                    contentDescription = "영수증",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(TossPrimaryColor)
                )
            }
        }
    }
}