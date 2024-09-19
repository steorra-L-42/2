package com.kimnlee.payment.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kimnlee.common.R
import org.w3c.dom.Text

@Composable
fun PaymentDetailScreen(
    bill : Map<String,Any>,
    onNavigateBack: () -> Unit
) {
    Button(onClick = onNavigateBack, modifier = Modifier.padding(10.dp) ) {
        Text("뒤로 가기")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
//            .background(Color.Red)

    ) {
            Text(
                text = "전자 영수증",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        Spacer(modifier = Modifier.padding(16.dp))
//        Text(
//            text = "${bill["transaction_unique_no"]} 번째 결제 상세 페이지",
//            style = MaterialTheme.typography.headlineMedium
//        )
//        Text(text = "${bill["transaction_unique_no"]}") // 번호
        // 기게 이름
        Text(text = "${bill["store_name"]}")
        // 소요 금액
        Row(
            verticalAlignment = Alignment.Bottom
        ){
            Text(text = "${bill["payment_balance"]}",
                style = MaterialTheme.typography.headlineMedium,
//                modifier = Modifier.background(Color.Red),
                textAlign = TextAlign.End
            )
            Text("원",style = MaterialTheme.typography.bodyLarge)
        }
        // 카드 그림
        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
            Image(painter = painterResource(id=R.drawable.credit_card_24px), contentDescription = "카드 그림",
                modifier = Modifier
                    .background(Color.Gray)
            )
            Text(text = "싸피 카드 (5612)")
        }
        Spacer(modifier = Modifier.padding(10.dp))
        HorizontalDivider()
        //공급가액 부가세 봉사료
        Column {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("공급가액")
                Text("${bill["payment_balance"]}")
            }
            // 부가세
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("부가세")
                Text("${bill["payment_balance"]}")
            }
            // 봉사료
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("봉사료")
                Text("${bill["payment_balance"]}")
            }
        }
        HorizontalDivider()
        // 결제금액
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("결제 금액")
            Text("${bill["payment_balance"]} + 부가세 + 봉사료")
        }
        HorizontalDivider()

        // 거래구분~ 결제금액
        Column {
            // 거래구분
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("거래구분")
                Text("싸피카드(5612)")
            }
            // 거래 유형
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("거래 유형")
                Text("국내 승인")
            }
            // 거래 일시
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("거래 일시")
                Row {
                    Text("${bill["transaction_date"]} ")

                    Text("${bill["transaction_time"]}")
                }
            }
            // 승인 번호
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("거래 일시")
                Row {
                    Text("${bill["transaction_date"]} ")

                    Text("${bill["transaction_time"]}")
                }
            }
            // 할부
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("할부")
                Text(text = "${bill["info"]}")
            }
            // 가맹점 번호
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("가맹점 번호")
                Text("761635802")
            }
            // 결제 금액
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("결제 금액")
                Text("${bill["payment_balance"]}")
            }
        }
        HorizontalDivider()

        // 상호 ~ 주소
        Column {
            // 상호
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("상호")
                Text(text = "${bill["store_name"]}")
            }
            // 사업자 번호
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("사업자 번호")
                Text("123-45-67890")
            }
            // 대표자명
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("대표자명")
                Text("싸피왕")
            }
            // 전화번호
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("전화번호")
                Text("02-1588-3366")
            }
            // 주소
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("주소")
                Text("서울특별시 송파구 잠실로 51-31")
            }
            // 가맹점 번호
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("가맹점 번호")
                Text("761635802")
            }
            // 결제 금액
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("결제 금액")
                Text("${bill["payment_balance"]}")
            }
        }
    }
}