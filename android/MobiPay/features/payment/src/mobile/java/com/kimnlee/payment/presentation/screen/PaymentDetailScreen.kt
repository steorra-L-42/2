package com.kimnlee.payment.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W100
import androidx.compose.ui.text.font.FontWeight.Companion.W900
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kimnlee.common.R
import org.w3c.dom.Text

@Composable
fun PaymentDetailScreen(
    bill: Map<String, Any>, onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState())
    ) {
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
        Text(text = "${bill["store_name"]}", style = MaterialTheme.typography.headlineMedium)
        // 소요 금액
        Row {
            Text(
                text = "${bill["payment_balance"]}",
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
            DetailItem(title = "공급가액", content = "${bill["payment_balance"]}")
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
        DetailItem(title = "결제금액", content = "${bill["payment_balance"]} + 부가세 + 봉사료")

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
                title = "거래 일시", content = "${bill["transaction_date"]} ${bill["transaction_time"]}"
            )
            DetailItem(title = "승인 번호", content = "31961357")
            DetailItem(title = "할부", content = "${bill["info"]}")
            DetailItem(title = "가맹점 번호", content = "761635802")
            DetailItem(title = "결제 금액", content = "${bill["payment_balance"]}")
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
            DetailItem(title = "상호", content = "${bill["store_name"]}")
            DetailItem(title = "사업자 번호", content = "123-45-67890")
            DetailItem(title = "대표자명", content = "싸피왕")
            DetailItem(title = "전화번호", content = "02-1588-3366")
            DetailItem("주소", "서울특별시 송파구 잠실로 51-31")
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

// 점선
@Composable
fun DashedDivider(
    thickness: Dp,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    phase: Float = 10f,
    intervals: FloatArray = floatArrayOf(10f, 10f),
    modifier: Modifier
) {
    Canvas(
        modifier = modifier
    ) {
        val dividerHeight = thickness.toPx()
        drawRoundRect(
            color = color, style = Stroke(
                width = dividerHeight, pathEffect = PathEffect.dashPathEffect(
                    intervals = intervals, phase = phase
                )
            )
        )
    }
}