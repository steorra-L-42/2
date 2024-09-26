import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kimnlee.common.components.MobiTheme
import com.kimnlee.common.R

// Custom Font Family
val pRegularFontFamily = FontFamily(Font(R.font.pregular))
val pBoldFontFamily = FontFamily(Font(R.font.pbold))
val pMediumFontFamily = FontFamily(Font(R.font.pmedium))

@Composable
fun PaymentSuccessScreen() {
    MobiTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Ensure the Card is at the bottom
        ) {

            Spacer(modifier = Modifier.height(66.dp))

            // Success Icon
            Image(
                painter = painterResource(id = R.drawable.success),
                contentDescription = "결제성공 아이콘",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Title
            Text(
                text = "결제 성공",
                fontSize = 28.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontFamily = pBoldFontFamily, // Use the custom bold font
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "결제 결과를 가맹점에 전달했어요.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontFamily = pRegularFontFamily // Use the custom regular font
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Payment Methods Section
            PaymentDetailsCard()

            Spacer(modifier = Modifier.height(80.dp))

            // Return to Main Button
            ReturnToMainButton()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PaymentDetailsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Payment details as rows
            PaymentDetailRow("승인번호", "435227483920")
            Spacer(modifier = Modifier.height(14.dp))
            PaymentDetailRow("일시", "2024/11/11 18:18:18")
            Spacer(modifier = Modifier.height(14.dp))
            PaymentDetailRow("결제 카드", "모비카드 8282")
            Spacer(modifier = Modifier.height(14.dp))
            PaymentDetailRow("금액", "10,000원")
            Spacer(modifier = Modifier.height(14.dp))
            PaymentDetailRow("부가세", "1,000원")
            Spacer(modifier = Modifier.height(14.dp))
            PaymentDetailRow("합계", "11,000원")
        }
    }
}

@Composable
fun ReturnToMainButton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.mobi_blue)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = "메인 화면으로 돌아가기",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            fontFamily = pMediumFontFamily // Use the custom medium font
        )
    }
}

@Composable
fun PaymentDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = TextStyle(
            fontSize = 16.sp,
            color = Color.Gray,
            fontFamily = pRegularFontFamily
        ))
        Text(value, style = TextStyle(
            fontSize = 16.sp,
            fontFamily = pMediumFontFamily
        ))
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PaymentSuccessScreenPreview() {
//    PaymentSuccessScreen()
//}
