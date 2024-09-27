package com.kimnlee.payment.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kimnlee.common.R
import com.kimnlee.payment.data.model.Merchant
import com.kimnlee.payment.data.model.MerchantTransaction

@Composable
fun PaymentDetailListScreen(
    transactions: List<MerchantTransaction>,
    merchants: List<Merchant>,
    onNavigateToDetail: (transaction : MerchantTransaction) -> Unit,
    onNavigateBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 시작
        Text(
            text = "결제",
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))
        // 더미 데이터를 사용
        LazyColumn(
//            modifier = Modifier.fillMaxWidth(),
        ){
            items(transactions) { transaction ->
                Row(
                    modifier = Modifier
                        .padding(8.dp) // 카드 간의 간격 설정
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column (modifier = Modifier
                        .weight(0.5f)
                    ){
                        // 가게이름
                        Text(
                            text = merchants.find { it.merchant_id == transaction.merchant_id }?.merchant_name ?: "Unknown Merchant",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        //시간 + 일시불
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ){
                            Text(transaction.transaction_date)
                            Text(transaction.transaction_time)
                            Text(transaction.info)
                        }
                    }
                    Text(
                        text = "${transaction.payment_balance}원",
                        modifier = Modifier
                            .weight(0.2f)
                            .padding(top=5.dp)
                            .fillMaxHeight(),
                    )
                    Box( // Box를 사용하여 이미지의 높이를 부모에 맞춤
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.1f)
                            .aspectRatio(1f) // 이미지의 종횡비를 유지하기 위해 aspectRatio를 사용
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.receipt_long_24px),
                            contentDescription = "영수증 사진",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                                .background(Color.Gray)
                                .clickable {onNavigateToDetail(transaction)}
                        )
                    }
                }
            }
        }
        // 끝
    }
}