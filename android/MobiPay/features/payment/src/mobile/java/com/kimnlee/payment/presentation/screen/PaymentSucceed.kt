import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.kimnlee.common.FCMData
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiPayTheme
import java.math.BigInteger
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.kimnlee.common.utils.moneyFormat

// Custom Font Family
val pRegularFontFamily = FontFamily(Font(R.font.pregular))
val pBoldFontFamily = FontFamily(Font(R.font.pbold))
val pMediumFontFamily = FontFamily(Font(R.font.pmedium))

@Composable
fun PaymentSucceedScreen(navController: NavController) {

    val context = LocalContext.current
    val fcmDataJson = (context as? Activity)?.intent?.getStringExtra("fcmData")

    val fcmDataExtra = fcmDataJson?.let { Gson().fromJson(it, FCMData::class.java) }

    MobiPayTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.height(66.dp))

            Image(
                painter = painterResource(id = R.drawable.success),
                contentDescription = "결제성공 아이콘",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "결제 성공",
                fontSize = 28.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontFamily = pBoldFontFamily,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "결제 결과를 가맹점에 전달했어요.",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontFamily = pRegularFontFamily
            )

            Spacer(modifier = Modifier.height(40.dp))

            PaymentDetailsCard(fcmDataExtra)

            Spacer(modifier = Modifier.height(80.dp))

            ReturnToMainButton(navController)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun taxCalc(originalAmount: BigInteger, tenOrOne: Int): BigInteger {
    return when (tenOrOne) {
        10 -> (originalAmount / BigInteger("11")) * BigInteger("10")
        1 -> originalAmount / BigInteger("11")
        else -> BigInteger.ZERO
    }
}

@Composable
fun PaymentDetailsCard(fcmData: FCMData?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            PaymentDetailRow("가맹점명", fcmData?.merchantName ?: "맥도날드 동탄나루점")
            Spacer(modifier = Modifier.height(14.dp))
            PaymentDetailRow("일시", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")))
            Spacer(modifier = Modifier.height(14.dp))
            PaymentDetailRow("결제 카드", "모비카드 8282")
            Spacer(modifier = Modifier.height(14.dp))
            PaymentDetailRow("금액", moneyFormat(taxCalc(BigInteger(fcmData?.paymentBalance), 10)))
            Spacer(modifier = Modifier.height(14.dp))
            PaymentDetailRow("부가세", moneyFormat(taxCalc(BigInteger(fcmData?.paymentBalance), 1)))
            Spacer(modifier = Modifier.height(14.dp))
            PaymentDetailRow("합계", moneyFormat(BigInteger(fcmData?.paymentBalance)))
        }
    }
}

@Composable
fun ReturnToMainButton(navController: NavController) {
    Card(
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.mobi_blue)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("home") {
                    popUpTo(0) {  // '0' indicates popping all the way to the root of the back stack
                        inclusive = true  // Removes all back stack entries
                    }
                    launchSingleTop = true  // Ensures that only a single instance of the home screen is in the stack
                }
            }
        ,
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
            fontFamily = pMediumFontFamily
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

