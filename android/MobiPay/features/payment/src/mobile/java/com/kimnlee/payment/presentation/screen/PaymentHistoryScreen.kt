package com.kimnlee.payment.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kimnlee.common.ui.theme.MobiBgGray
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.ui.theme.MobiTextDarkGray
import com.kimnlee.common.utils.formatDateTime
import com.kimnlee.common.utils.moneyFormat
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiBgWhite
import com.kimnlee.payment.data.model.PaymentHistoryItem
import com.kimnlee.payment.presentation.viewmodel.PaymentHistoryState
import com.kimnlee.payment.presentation.viewmodel.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryScreen(
    onNavigateBack: () -> Unit,
    paymentViewModel: PaymentViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val paymentHistoryState by paymentViewModel.paymentHistory.collectAsState()

    LaunchedEffect(Unit) {
        paymentViewModel.loadPaymentHistory()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "📜",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily(Font(R.font.emoji)),
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "결제 내역",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MobiTextAlmostBlack
                        )
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
        when (paymentHistoryState) {
            is PaymentHistoryState.Success -> {
                val paymentHistory = (paymentHistoryState as PaymentHistoryState.Success).data
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding() + 16.dp,
                        bottom = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(paymentHistory.items) { item ->
                        ItemCard(
                            item = item,
                            onNavigateToDetail = { onNavigateToDetail(item.transactionUniqueNo) }
                        )
                    }
                }
            }
            is PaymentHistoryState.NoContent -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "결제 내역이 없어요",
                            style = MaterialTheme.typography.titleLarge,
                            color = MobiTextDarkGray,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .padding(bottom = 8.dp)
                        )
                        Text(
                            text = "🤔",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily(Font(R.font.emoji))
                        )
                    }
                }
            }
            PaymentHistoryState.Initial -> {}
        }
    }
}

@Composable
fun ItemCard(
    item: PaymentHistoryItem,
    onNavigateToDetail: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onNavigateToDetail),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MobiBgWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFE8F3FF), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.merchantName.first().toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF3182F6)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.merchantName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MobiTextAlmostBlack,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDateTime(item.transactionDate, item.transactionTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MobiTextDarkGray
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = moneyFormat(item.paymentBalance.toBigInteger()),
                    style = MaterialTheme.typography.titleMedium,
                    color = MobiTextAlmostBlack,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "결제완료",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF3182F6)
                )
            }
        }
    }
}