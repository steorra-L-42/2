package com.kimnlee.mobipay.presentation.screen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel

@Composable
fun HomeScreen(
    viewModel: LoginViewModel,
    navController: NavController,
    context: Context
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    var lastLocationText by remember { mutableStateOf("마지막 위치: 불러오는 중...") }


    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate("auth") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val lastLocation = getLastLocation(context)
        lastLocationText = if (lastLocation != null) {
            "주차 위치: 위도 ${lastLocation.first}, 경도 ${lastLocation.second}"
        } else {
            "주차 위치: 위치 정보가 없습니다"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome to MobiPay (모바일 화면)",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.testLogout() // 현재 테스트 로그아웃이라서 나중에 백이랑 연결되면 일반 logout 메서드로 바꾸면 됨
            }
        ) {
            Text("로그아웃")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("vehiclemanagement") }
        ) {
            Text("나의 차 관리")
        }
        Spacer(modifier = Modifier.height(8.dp))
//        Button(
//            onClick = { navController.navigate("memberinvitation") }
//        ) {
//            Text("멤버 초대")
//        }
        Spacer(modifier = Modifier.height(16.dp))
        // Display the last known location
        Text(text = lastLocationText)

        Button(
            onClick = {
                // Create an Intent to launch PaymentSucceed activity
//                val intent = Intent(context, PaymentSucceed::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // Add this line
//                ContextCompat.startActivity(context, intent, null)
                navController.navigate("paymentsucceed")
            }
        ) {
            Text("Payment Succeed")
        }
    }
}


private fun getLastLocation(context: Context): Pair<Double, Double>? {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("last_location", Context.MODE_PRIVATE)
    val lat = sharedPreferences.getString("last_latitude", null)
    val lng = sharedPreferences.getString("last_longitude", null)

    return if (lat != null && lng != null) {
        Pair(lat.toDouble(), lng.toDouble())
    } else {
        null
    }
}
